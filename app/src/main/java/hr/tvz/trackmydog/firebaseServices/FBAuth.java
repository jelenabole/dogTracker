package hr.tvz.trackmydog.firebaseServices;

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

import hr.tvz.trackmydog.MainActivity;
import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.activities.LoginActivity;
import hr.tvz.trackmydog.services.MyCallback;
import hr.tvz.trackmydog.services.NotificationService;
import hr.tvz.trackmydog.localDB.User;
import hr.tvz.trackmydog.models.userModel.DogInfo;
import hr.tvz.trackmydog.models.userModel.CurrentUser;

public class FBAuth {

    private static final String TAG = "FB Auth";

    public static FirebaseAuth mAuth;
    private static User localUser;
    private static CurrentUser currentUserFB;

    // needed for logout (deleting listener):
    private static DatabaseReference userRef;
    private static ValueEventListener userListener;

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

    // get user key (firebase uid):
    public static String getUserKey() {
        return currentUserFB.getKey();
    }

    // initialize firebase auth at the beginning:
    public static void initializeFirebaseAuth() {
        Log.d(TAG, "firebase service init");
        mAuth = FirebaseAuth.getInstance();
    }


    /**
     * Makes a new user from CurrentUser (got from firebase listener).
     * Infos are saved locally so that they can be grabbed when needed.
     */
    private static void addLocalUser() {
        User user = new User();

        user.setActive(true);
        user.setDisplayName(currentUserFB.getName());
        user.setEmail(currentUserFB.getEmail());
        user.setKey(currentUserFB.getKey());

        user.save();
    }





    /* Log in = with google auth - register or login */

    /**
     * Check if user exists in firebase DB, get data or add new one.
     * @param firebaseUser - current mAuth user
     * @param context - current app context
     */
    public static void getFirebaseUser(FirebaseUser firebaseUser, Context context) {
        // find info in FB and add locally if needed
        checkIfUserExistsInFirebase(firebaseUser, context, new MyCallback() {
            @Override
            public void startIntent(Context context) {
                Log.d(TAG, "user listeners set = start MainActivity");
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);

                // back button closes the app (doesnt return to login)
                ((Activity) context).finish();
            }
        });
    }

    /**
     * Get the user (detailed) info from the firebase and write the user info locally if it
     * doesnt exist already. Also write to firebase if it doesnt exist.
     *
     * Check user token. If it's not the same as the current app token, then change it on firebase
     * and change it for every dog (token is used for server notifications).
     * @param user = firebase user, needed for email
     * @param callback = called to save the user locally
     */
    private static void checkIfUserExistsInFirebase(final FirebaseUser user, final Context context,
                final MyCallback callback) {
        Log.d(TAG, "get user from firebase (by email) or create new one");
        final String userEmail = user.getEmail();

        // find current user or make new one
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean found = false;
                    String token = NotificationService.getAppToken();

                    for (DataSnapshot userSnaps : dataSnapshot.getChildren()) {
                        if (userSnaps.child("email").getValue().equals(userEmail)) {
                            found = true;
                            currentUserFB = userSnaps.getValue(CurrentUser.class);
                            currentUserFB.setKey(userSnaps.getKey());

                            // check if token is the same
                            // if null or not equal, save new one to FB:
                            if (currentUserFB.getToken() == null ||
                                    !currentUserFB.getToken().equals(token)) {
                                Log.e(TAG, "token is null or not valid - save new one on FB");

                                // change user token:
                                usersRef.child(user.getUid()).child("token").setValue(token);

                                // change all dogs tokens:
                                if (currentUserFB.getDogs() != null) {
                                    DatabaseReference dogsRef = FirebaseDatabase.getInstance().getReference("dogs");
                                    for (DogInfo dog : currentUserFB.getDogs()) {
                                        dogsRef.child(dog.getKey()).child("token").setValue(token);
                                    }
                                }
                            }

                            break;
                        }
                    }

                    // if user is not found:
                    if (!found) {
                        Log.d(TAG, "user not found fB = register new one");
                        currentUserFB = new CurrentUser();
                        currentUserFB.setEmail(user.getEmail());
                        currentUserFB.setToken(token);
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
            userRef = FirebaseDatabase.getInstance()
                    .getReference("users/" + currentUserFB.getKey());

            userListener = userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // data not found (eg user deleted from firebase)
                        Log.e(TAG, " *** user Ref Listener - user not found");
                        // happens only when user deleted from database (not through this app)
                        // TODO - notify the user about the error
                        // error = user data was deleted

                        // sign out the user and delete this listener
                        logoutUser();
                    } else {
                        Log.w(TAG, " *** current user listener = user changed");
                        Log.d(TAG, currentUserFB.toString());
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

    // deletes backstack (all) and starts Login activity
    // called from a user-listener (user deleted) and on Logout button pressed
    // deletes the "current user listener"
    public static void logoutUser() {
        Log.d(TAG, "sign out user and return to Login Activity");
        // firebase - signout
        mAuth.signOut();
        if (userListener != null)
            userRef.removeEventListener(userListener);
        currentUserFB = null;


        // TODO - delete local user:
        /*
        // deactivate local user
        localUser.setActive(false);
        localUser.save();
        localUser = null;
        */

        Context context = MyApplication.getContext();
        Intent intent = new Intent(context, LoginActivity.class);
        // flags = everything is cleared (new task) before the activity started (new root)
        // .. old activities are finished.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        // back button closes the app (doesnt return to main activity)
        // ((Activity) context).finish();
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
