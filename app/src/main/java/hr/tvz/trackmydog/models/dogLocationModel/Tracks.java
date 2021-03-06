package hr.tvz.trackmydog.models.dogLocationModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

import hr.tvz.trackmydog.utils.TimeDistanceUtils;

@IgnoreExtraProperties
public class Tracks implements Serializable {

    private double latitude;
    private double longitude;
    private long time;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    // for comparing objects:
    public boolean isEqual(Tracks tracks) {
        return (tracks.getLatitude() == this.latitude && tracks.getLongitude() == this.longitude);
    }

    @Override
    public String toString() {
        String str = "";

        str += " - " + "latitude: " + latitude;
        str += " - " + "longitude: " + longitude;
        // str += " - " + "time: " + time;
        str += " - " + "time: " + TimeDistanceUtils.converTimeToReadable(time);

        return str;
    }
}
