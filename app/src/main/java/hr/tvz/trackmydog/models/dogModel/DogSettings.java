package hr.tvz.trackmydog.models.dogModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class DogSettings implements Serializable {

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
