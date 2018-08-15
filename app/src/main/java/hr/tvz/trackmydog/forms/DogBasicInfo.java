package hr.tvz.trackmydog.forms;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.userModel.BasicDog;

@IgnoreExtraProperties
public class DogBasicInfo implements Serializable {

    private String name;
    private String breed;
    private Integer age;
    private String photoURL;
    private String color;

    // additional info:
    private Integer height;
    private Integer weight;
    private String gender;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // just fields that we are updating:
    public void mapDog(BasicDog dog) {
        // map all fields:
        name = dog.getName();
        breed = dog.getBreed();
        age = dog.getAge();
        height = dog.getHeight();
        weight = dog.getWeight();
        gender = dog.getGender();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("breed", breed);
        result.put("age", age);
        result.put("height", height);
        result.put("weight", weight);
        result.put("gender", gender);

        return result;
    }

    @Override
    public String toString() {
        String str = "********** \n";

        str += " - " + "name: " + name;
        str += " - " + "breed: " + breed;
        str += " - " + "age: " + age;
        str += " - " + "color: " + color;
        str += " - " + "photoURL: " + photoURL;

        str += "\n - " + "height: " + height;
        str += " - " + "weight: " + weight;
        str += " - " + "gender: " + gender;

        return str;
    }
}
