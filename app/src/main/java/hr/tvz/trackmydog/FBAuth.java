package hr.tvz.trackmydog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import hr.tvz.trackmydog.activities.LoginActivity;
import hr.tvz.trackmydog.firebaseWait.MyCallback;
import hr.tvz.trackmydog.localDB.DbFlowApp;
import hr.tvz.trackmydog.localDB.FirebaseTokenService;
import hr.tvz.trackmydog.localDB.User;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class FBAuth {

    private static final String TAG = "FB Auth";

    public static FirebaseAuth mAuth;
    private static User localUser;
    private static CurrentUser currentUserFB;

    /* ERROR - check if this works */
    private static Integer currentDogIndex;
    public static Integer getCurrentDogIndex() {
        return currentDogIndex;
    }
    public static void setCurrentDogIndex(Integer index) {
        currentDogIndex = index;
    }
    /* END check */

    // get FB user:
    public static CurrentUser getCurrentUserFB() {
        return currentUserFB;
    }

    // get local user:
    public static User getLocalUser() {
        return localUser;
    }

    // get user key (firebase uid):
    public static String getUserKey() {
        return currentUserFB.getKey();
    }

    // initialize firebase auth at the beginning:
    public static void initializeFirebaseAuth() {
        Log.d(TAG, "firebase service init");
        mAuth = FirebaseAuth.getInstance();
    }

    // check if user (fb) is logged in - also check locally:
    public static void checkIfUserIsLoggedIn(Context context, MyCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "user logged in - get info and show main activity");
            loginUser(currentUser, context);
        } else {
            Log.d(TAG, "user isn't logged = show google sign-in option");
            callback.startIntent(context);
        }
    }

    /**
     * Makes a new user from CurrentUser (got from firebase listener).
     * Infos are saved locally so that they can be grabbed when needed.
     */
    private static void addLocalUser() {
        User user = new User();

        user.setActive(true);
        user.setCode(currentUserFB.getCode());
        user.setDisplayName(currentUserFB.getDisplayName());
        user.setEmail(currentUserFB.getEmail());
        user.setKey(currentUserFB.getKey());

        user.save();
    }






    /* Log in = with google auth - register or login */

    public static void loginUser(FirebaseUser firebaseUser, Context context) {
        // find info in FB and add locally if needed
        checkIfUserExistsInFirebase(firebaseUser, new MyCallback() {
            @Override
            public void startIntent(Context context) {
                Log.d(TAG, "user listeners set = start MainActivity");
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);

                // back button closes the app (doesnt return to login)
                ((Activity) context).finish();
            }
        }, context);
    }

    // deletes backstack (all) and starts Login activity
    // called from a user-listener (user deleted) and on Logout button pressed
    public static void logoutUser() {
        Log.d(TAG, "sign out user and return to Login Activity");
        // firebase - signout
        mAuth.signOut();
        currentUserFB = null;

        // deactivate local user
        localUser.setActive(false);
        localUser.save();
        localUser = null;

        Context context = DbFlowApp.getContext();
        Intent intent = new Intent(context, LoginActivity.class);
        // flags = everything is cleared (new task) before the activity started (new root)
        // .. old activities are finished.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        // back button closes the app (doesnt return to main activity)
        // ((Activity) context).finish();
    }

    /**
     * Get the user (detailed) info from the firebase and write the user info locally if it
     * doesnt exist already. Also write to firebase if it doesnt exist.
     * Same function with callback. After the user info is available from user
     * @param user = firebase user, needed for email
     * @param callback = called to save the user locally
     */
    private static void checkIfUserExistsInFirebase(final FirebaseUser user,
                final MyCallback callback, final Context context) {
        Log.d(TAG, "get user from firebase (by email)");
        final String userEmail = user.getEmail();

        // get all users, return user info
        // if user is not found, make new user
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean found = false;
                    for (DataSnapshot userSnaps : dataSnapshot.getChildren()) {
                        if (userSnaps.child("email").getValue().equals(userEmail)) {
                            found = true;
                            currentUserFB = userSnaps.getValue(CurrentUser.class);
                            currentUserFB.setKey(userSnaps.getKey());
                            break;
                        }
                    }

                    // if user is not found:
                    if (!found) {
                        Log.d(TAG, "user not found fB = register new one");
                        currentUserFB = new CurrentUser();
                        currentUserFB.setEmail(user.getEmail());
                        currentUserFB.setToken(FirebaseTokenService.getAppToken());
                        currentUserFB.setFollow(true);
                        // save that user to firebase and get back the key:
                        usersRef.child(user.getUid()).setValue(currentUserFB);
                    }

                    getLocalUser(currentUserFB.getEmail());
                    setListenerToCurrentUser();
                    callback.startIntent(context);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Toast.makeText(getApplicationContext(),
                    // databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Set FB listener on user, so that the info is updated accordingly.
     * If user is deleted (on FB), remove the listener and return to Login Activity.
     */
    private static void setListenerToCurrentUser() {
        Log.d(TAG, "set FB listener to current user");
        if (currentUserFB.getKey() != null) {
            final DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users/" + currentUserFB.getKey());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // data not found (eg user deleted from firebase)
                        Log.e(TAG, " *** user Ref Listener - user not found");
                        // TODO - notify the user about the error

                        // sign out the user and delete listener
                        userRef.removeEventListener(this);
                        logoutUser();
                    } else {
                        currentUserFB = dataSnapshot.getValue(CurrentUser.class);
                        currentUserFB.setKey(dataSnapshot.getKey());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    // get local user, if it doesnt exist, add new one:
    // TODO - is this needed ??
    private static void getLocalUser(String email) {
        Log.d(TAG, "get local user info");
        boolean found = false;

        // find user by email (make it active if not)
        // deactivate others:
        List<User> users = SQLite.select().from(User.class).queryList();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                if (!user.isActive()) {
                    user.setActive(true);
                    user.save();
                }
                found = true;
                localUser = user;
            } else {
                // set user as non-active:
                if (user.isActive()) {
                    user.setActive(false);
                    user.save();
                }
            }
        }

        // if user is not found, add one by currentUserFB:
        if (!found) {
            Log.d(TAG, "local user not found - add new one");
            addLocalUser();
        }
    }

}
