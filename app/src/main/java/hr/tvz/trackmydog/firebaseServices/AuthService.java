package hr.tvz.trackmydog.firebaseServices;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.models.forms.NewUserForm;
import hr.tvz.trackmydog.models.userModel.CurrentUser;
import hr.tvz.trackmydog.models.userModel.DogInfo;

public class AuthService {

    private static final String TAG = "Auth Service";
    private static final String userLink = "users/";

    // register:
    public static void addUser(NewUserForm user) {
        Log.d(TAG, "save (register) new user: " + user);

        FirebaseDatabase.getInstance()
                .getReference(userLink + MyApplication.getUserKey())
                .setValue(user);
    }

    public static void changeToken(CurrentUser user, String token) {
        Log.d(TAG, "change user token to: " + token);

        // change user token:
        FirebaseDatabase.getInstance().getReference(userLink + MyApplication.getUserKey())
                .child("token").setValue(token);

        // change all dogs tokens:
        if (user.getDogs() != null) {
            DatabaseReference dogsRef = FirebaseDatabase.getInstance().getReference("dogs");
            for (DogInfo dog : user.getDogs()) {
                dogsRef.child(dog.getKey()).child("token").setValue(token);
            }
        }
    }
}
