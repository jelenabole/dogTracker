package hr.tvz.trackmydog.models.userModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class CurrentUser implements Serializable {

    private String key; // email
    private String name;
    private String email;
    private String photoURL;
    private boolean follow;

    // additional info:
    private String fullName;
    private String city;
    private String phoneNumber;
    private String gender;

    private String token; // fb messaging token
    private List<DogInfo> dogs;
    private HashMap<String, SafeZone> safeZones;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<DogInfo> getDogs() {
        return dogs;
    }

    public void setDogs(List<DogInfo> dogs) {
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
        str += " - " + "name: " + name;
        str += " - " + "email: " + email;

        str += "\n - " + "city: " + city;
        str += " - " + "gender: " + gender;
        str += " - " + "phone number: " + phoneNumber;

        str += "\n - " + "token: " + token;
        str += "\n - " + "follow user: " + follow;
        str += " - " + "photoURL: " + photoURL;

        str += " - " + "dogs: \n" + dogs;
        str += " - " + "safe zones: \n" + safeZones;

        return str;
    }
}
