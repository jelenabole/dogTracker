package hr.tvz.trackmydog.userModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class CurrentUser implements Serializable {

    private String key; // email
    private String displayName;
    private String email;
    private String code;
    private String photoURL;
    private boolean follow;

    // additional info:
    private String location;

    private String token; // fb messaging token
    private List<BasicDog> dogs;
    private HashMap<String, SafeZone> safeZones;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        location = location;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<BasicDog> getDogs() {
        return dogs;
    }

    public void setDogs(List<BasicDog> dogs) {
        this.dogs = dogs;
    }

    public HashMap<String, SafeZone> getSafeZones() {
        return safeZones;
    }

    public void setSafeZones(HashMap<String, SafeZone> safeZones) {
        this.safeZones = safeZones;
    }

    @Override
    public String toString() {
        String str = "********** \n";

        str += " - " + "key: " + key;
        str += " - " + "displayName: " + displayName;
        str += " - " + "email: " + email;
        str += " - " + "location: " + location;
        str += " - " + "code: " + code;
        str += " - " + "photoURL: " + photoURL;
        str += " - " + "follow user: " + follow;

        str += " - " + "dogs: \n" + dogs;
        str += " - " + "safe zones: \n" + safeZones;

        return str;
    }
}
