package hr.tvz.trackmydog.models.dogLocationModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class DogMarker implements Serializable {

    // needed for finding right marker (added after FB listener):
    private Integer index;
    private String name;
    private String color;

    private String key;
    private ShortLocation location;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ShortLocation getLocation() {
        return location;
    }

    public void setLocation(ShortLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        String str = "";

        str += "key: " + key;
        str += " - " + "index: " + index;
        str += " - " + "color: " + color;
        str += "\n - " + "location: " + location;

        return str;
    }
}
