package hr.tvz.trackmydog.models.forms;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class DogSettingsForm implements Serializable {

    private int interval;
    private String locationName;
    private int range;
    private double latitude;
    private double longitude;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("interval", interval);
        result.put("locationName", locationName);
        result.put("range", range);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

    @Override
    public String toString() {
        String str = "";

        str += "\n " + "interval: " + interval;
        str += " - " + "location name: " + locationName;
        str += " - " + "range: " + range;
        str += " - " + "latitude: " + latitude;
        str += " - " + "longitude: " + longitude;

        return str;
    }
}
