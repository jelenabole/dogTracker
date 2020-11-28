package hr.tvz.trackmydog.firebaseServices;

import android.util.Log;
import com.google.firebase.database.FirebaseDatabase;

import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.callbacks.ActivityCallback;
import hr.tvz.trackmydog.models.forms.SafeZoneForm;
import hr.tvz.trackmydog.models.forms.UserForm;

public class UserService {

    private static final String TAG = "User Service";
    private static final String userLink = "users/";

    public static void saveUser(UserForm user, final ActivityCallback callback) {
        Log.d(TAG, "save user: " + user);

        FirebaseDatabase.getInstance().getReference(userLink + MyApplication.getUserKey())
            .updateChildren(user.toMap(), (databaseError, databaseReference) -> {
                // send flag if there was an error:
                callback.closeIntent(databaseError != null);
            });
    }

    public static void saveSafeZone(SafeZoneForm safeZone, final ActivityCallback callback) {
        Log.d(TAG, "save safe zone: " + safeZone);

        FirebaseDatabase.getInstance()
                .getReference("users/" + MyApplication.getUserKey() + "/safeZones")
                .push().setValue(safeZone.toMap(), (databaseError, databaseReference) -> {
                    // send flag if there was an error:
                    callback.closeIntent(databaseError != null);
                });
    }

}
