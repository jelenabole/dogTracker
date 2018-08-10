package hr.tvz.trackmydog;

// singleton class (check this):
// https://medium.com/exploring-code/how-to-make-the-perfect-singleton-de6b951dfdb0

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import hr.tvz.trackmydog.firebaseWait.MyCallback;
import hr.tvz.trackmydog.localDB.User;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class FBAuth {

    // init the firebase
    // check the accounts (current user) and stuff
    private static User localUser;
    private static CurrentUser currentUserFB;


    public static FirebaseAuth mAuth;

    // get FB user:
    public static CurrentUser getCurrentUserFB() {
        return currentUserFB;
    }

    // get local user:
    public static User getLocalUser() {
        return localUser;
    }

    // initialize firebase auth at the beginning:
    public static void initializeFirebaseAuth() {
        // [START initialize_auth]
        System.out.println("*** firebase service init");
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // check if user (fb) is logged in - also check locally:
    public static void checkIfUserIsLoggedIn(Context context) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // check current local user, and get firebase user info:
            System.out.println("USER POSTOJI ***");
            // TODO - login
            loginUser(currentUser, context);
        } else {
            // TODO - register
            System.out.println("USER NE POSTOJI ***");
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


    /**
     * Set FB listener on user, so that the info is updated accordingly.
     */
    private static void setUserListener() {
        if (localUser.getKey() != null) {
            FirebaseDatabase.getInstance().getReference("users/" + localUser.getKey())
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // TODO - na svaku promjenu namapirati usera:
                    currentUserFB = dataSnapshot.getValue(CurrentUser.class);
                    currentUserFB.setKey(dataSnapshot.getKey());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }






    /* Log in = with google auth - register or login */

    public static void loginUser(FirebaseUser firebaseUser, Context context) {
        // find info in FB and add locally if needed
        checkIfUserExistsInFirebase(firebaseUser, new MyCallback() {
            @Override
            public void startIntent(Context context) {
                // start intent here:
                // TODO - start intent after everything is set up:
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);

            }
        }, context);
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
        // TODO - find user by email (single event):
        final String userEmail = user.getEmail();

        // get all users, return user info
        // if user is not found, make new user
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean found = false;
                    for (DataSnapshot userSnaps : dataSnapshot.getChildren()) {
                        // TODO - find user by email:
                        if (userSnaps.child("email").getValue().equals(userEmail)) {
                            found = true;
                            currentUserFB = userSnaps.getValue(CurrentUser.class);
                            currentUserFB.setKey(userSnaps.getKey());
                            break;
                        }
                    }

                    // if user is not found:
                    if (!found) {
                        // add new user, and snap its key:
                        // return this user as current user:

                        // TODO - get user info:
                        currentUserFB = new CurrentUser();
                        currentUserFB.setEmail(user.getEmail());
                        currentUserFB.setFollow(true);
                        // save that user to firebase and get back the key:
                        usersRef.child(user.getUid()).setValue(currentUserFB);
                    }

                    // TODO - set listener to user and get local user (or add new one)
                    // and start intent

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

    // TODO - check key locally or current user ??? error
    private static void setListenerToCurrentUser() {
        System.out.println("*** set listener to current user");
        if (currentUserFB.getKey() != null) {
            FirebaseDatabase.getInstance().getReference("users/" + currentUserFB.getKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // TODO - na svaku promjenu namapirati usera:
                        currentUserFB = dataSnapshot.getValue(CurrentUser.class);
                        currentUserFB.setKey(dataSnapshot.getKey());
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
        System.out.println("*** get local user by email");
        boolean found = false;
        // find user by email (make it active if not)
        // deactivate others:
        List<User> users = SQLite.select().from(User.class).queryList();
        for (User user : users) {
            System.out.println(user);
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
            System.out.println("*** add new user locally");
            addLocalUser();
        }
    }



    /* OTHER METHODS */



    /*
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI();
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI();
                    }
                });
    }

    // TODO - start the main activity, with that new user:
    // TODO - change on the logout
    // returns null when google login failed (e.g. when clicked outside of pop up)
    private void updateUI() {
        if (user == null) {
            return;
        }
        // TODO - only userEmail is needed:
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    */

}
