package hr.tvz.trackmydog.firebaseServices;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import hr.tvz.trackmydog.models.forms.SafeZoneForm;
import hr.tvz.trackmydog.models.forms.ServiceSettingsForm;

public class DogService {

    private static final String TAG = "Dog Service";
    private static final String dogLink = "dogs/";

    public static void saveSettings(ServiceSettingsForm settings, String dogKey) {
        Log.d(TAG, "save dog's settings: " + dogKey);
        FirebaseDatabase.getInstance().getReference(dogLink + dogKey + "/settings")
            .updateChildren(settings.toMap());
    }

    public static void saveSafezone(SafeZoneForm safezone, String dogKey) {
        Log.d(TAG, "save dog's safezone: " + dogKey);
        FirebaseDatabase.getInstance().getReference(dogLink + dogKey + "/safezone")
                .setValue(safezone);
        }
}