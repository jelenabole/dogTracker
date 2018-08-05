package hr.tvz.trackmydog.userModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class BasicDog implements Serializable {

    private int index;
    private String name;
    private Integer age;
    private String photoURL;
    private String color;

    // TODO - only settings needed for user:
    // TODO - add range of motion:
    /*
    private CurrentLocation location;
    private Tracking tracks;
    */
    private Settings settings;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /*
    public CurrentLocation getLocation() {
        return location;
    }

    public void setLocation(CurrentLocation location) {
        this.location = location;
    }

    public Tracking getTracks() {
        return tracks;
    }

    public void setTracks(Tracking tracks) {
        this.tracks = tracks;
    }
    */

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        String str = "********** \n";

        str += "\n " + "index: " + index;
        str += " - " + "name: " + name;
        str += " - " + "age: " + age;
        str += " - " + "photoURL: " + photoURL;

        str += " - " + "settings: " + settings;

        // str += " - " + "current location: \n" + location;

        return str;
    }
}
