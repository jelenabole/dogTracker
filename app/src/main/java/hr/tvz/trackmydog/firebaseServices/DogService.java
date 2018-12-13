package hr.tvz.trackmydog.firebaseServices;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import hr.tvz.trackmydog.models.forms.DogSettingsForm;

public class DogService {

    private static final String TAG = "Dog Service";
    private static final String dogLink = "dogs/";

    public static void saveSettings(DogSettingsForm settings, String dogKey) {
        Log.d(TAG, "save dog settings: " + dogKey);

        FirebaseDatabase.getInstance().getReference(dogLink + dogKey + "/settings")
            .updateChildren(settings.toMap());
    }
}