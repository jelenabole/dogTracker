package hr.tvz.trackmydog.models.dogModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

import hr.tvz.trackmydog.models.userModel.Settings;

@IgnoreExtraProperties
public class Dog implements Serializable {

    private Integer index;
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
    private Settings settings;


    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
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
