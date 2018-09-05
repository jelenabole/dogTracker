package hr.tvz.trackmydog.forms;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class DogForm implements Serializable {

    private String key;
    private Integer index;
    private String name;
    private String color;

    // additional info:
    private String breed;
    private Integer age;
    private String photoURL;

    private Integer height;
    private Integer weight;
    private String gender;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("index", index);
        result.put("key", key);
        result.put("name", name);
        result.put("color", color);

        result.put("breed", breed);
        result.put("age", age);
        result.put("photoURL", photoURL);

        result.put("height", height);
        result.put("weight", weight);
        result.put("gender", gender);

        return result;
    }

    @Override
    public String toString() {
        String str = "********** \n";

        str += " - " + "index: " + index;
        str += " - " + "key: " + key;
        str += " - " + "name: " + name;
        str += " - " + "color: " + color;

        str += "\n - " + "breed: " + breed;
        str += " - " + "age: " + age;
        str += " - " + "photoURL: " + photoURL;

        str += "\n - " + "height: " + height;
        str += " - " + "weight: " + weight;
        str += " - " + "gender: " + gender;

        return str;
    }
}
