package hr.tvz.trackmydog.newDogModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;

@IgnoreExtraProperties
public class DogWithLocation implements Serializable {

    // needed for finding right marker (added after FB listener):
    private Integer index;
    private String name;
    private String color;

    private String key;
    private ShortLocation location;
    private HashMap<String, Tracks> tracks;

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

    public HashMap<String, Tracks> getTracks() {
        return tracks;
    }

    public void setTracks(HashMap<String, Tracks> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        String str = "";

        str += "\n " + "key: " + key;
        str += " - " + "index: " + index;
        str += " - " + "color: " + color;
        str += "\n - " + "location: " + location;
        if (tracks != null) {
            str += "\n - " + "number of tracks: " + tracks.size();
        }

        return str;
    }
}
