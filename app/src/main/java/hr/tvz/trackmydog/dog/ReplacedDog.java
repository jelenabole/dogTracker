package hr.tvz.trackmydog.dog;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

import hr.tvz.trackmydog.dogModel.CurrentLocation;
import hr.tvz.trackmydog.dogModel.Tracking;
import hr.tvz.trackmydog.userModel.Settings;

@IgnoreExtraProperties
public class ReplacedDog implements Serializable {

    private int index;
    private String code;
    private String key;

    // TODO - add these:
    // location, locations, settings

    private String name;
    private String breed;
    private Integer age;
    private String photoURL;
    private String color;

    // additional info:
    private Integer height;
    private Integer weight;
    private Float miles;
    private Date dateOfBirth;
    private String gender;

    private CurrentLocation location;
    private Tracking tracks;
    private Settings settings;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
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

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Float getMiles() {
        return miles;
    }

    public void setMiles(Float miles) {
        this.miles = miles;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

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

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        String str = "";

        str += "\n " + "index: " + index;
        str += " - " + "name: " + name;
        str += " - " + "breed: " + breed;
        str += " - " + "age: " + age;
        str += " - " + "photoURL: " + photoURL;

        // str += " - " + "current location: \n" + location;

        return str;
    }
}
