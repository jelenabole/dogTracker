package hr.tvz.trackmydog.firebaseServices;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.activities.LoginActivity;

public class FBAuthService {

    private static final String TAG = "FB Auth";

    public static FirebaseAuth mAuth;

    /**
     * Initializes firebase auth at the beginning of the app:
     */
    public static void initializeFirebaseAuth() {
        Log.d(TAG, "firebase service init");
        mAuth = FirebaseAuth.getInstance();
    }

    /* Log in and check user */

    // start app with the data, or show loggin layout:
    public static boolean isUserLoggedIn() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        Log.d(TAG, "is user logged in: " + firebaseUser);
        return firebaseUser != null;
    }

    // start app with the data, or show loggin layout:
    public static String getUserKey() {
        return mAuth.getCurrentUser().getUid();
    }

    // start app with the data, or show loggin layout:
    public static String getUserEmail() {
        return mAuth.getCurrentUser().getEmail();
    }


    /* Log out */

    // deletes backstack (all) and starts Login activity
    // called from a user-listener (user deleted) and on Logout button pressed
    // deletes the "current user listener"
    public static void logoutUser() {
        Log.d(TAG, "sign out user and return to Login Activity");
        // firebase - signout
        mAuth.signOut();

        Context context = MyApplication.getContext();
        Intent intent = new Intent(context, LoginActivity.class);
        // flags = everything is cleared (new task) before the activity started (new root)
        // .. old activities are finished.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        // back button closes the app (doesnt return to main activity)
        // ((Activity) context).finish();
    }

}
