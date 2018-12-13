package hr.tvz.trackmydog.firebaseServices;

public interface DogSettingsCallback {

    // string or number (interval or location name)
    void saveSettings(String string);
}