package hr.tvz.trackmydog.models.forms;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserForm implements Serializable {

    private String name;
    private String photoURL;
    private String city;
    private String phoneNumber;
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("photoURL", photoURL);
        result.put("city", city);
        result.put("phoneNumber", phoneNumber);
        result.put("gender", gender);

        return result;
    }

    @Override
    public String toString() {
        String str = "********** \n";

        str += " - " + "name: " + name;
        str += " - " + "photoURL: " + photoURL;
        str += "\n - " + "city: " + city;
        str += " - " + "phone number: " + phoneNumber;
        str += " - " + "gender: " + gender;

        return str;
    }
}
