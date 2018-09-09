package hr.tvz.trackmydog.userModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.tvz.trackmydog.HelperClass;

@IgnoreExtraProperties
public class CurrentUser implements Serializable {

    private String key; // email
    private String name;
    private String email;
    private String code;
    private String photoURL;
    private boolean follow;

    // additional info:
    private String city;
    // TODO - location = always null ??
    private String location;
    private String phoneNumber;
    private String gender;

    private String token; // fb messaging token
    private List<BasicDog> dogs;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        str += " - " + "email: " + email;
        str += " - " + "code: " + code;

        str += "\n - " + "city: " + city;
        str += " - " + "location: " + location;
        str += "\n - " + "name: " + name;
        str += " - " + "gender: " + gender;
        str += " - " + "phone number: " + phoneNumber;

        str += "\n - " + "token: " + token;
        str += "\n - " + "follow user: " + follow;
        str += " - " + "photoURL: " + photoURL;

        str += " - " + "dogs: \n" + dogs;
        str += " - " + "safe zones: \n" + safeZones;

        return str;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("city", city);
        result.put("phoneNumber", phoneNumber);
        result.put("gender", gender);

        return result;
    }

}
