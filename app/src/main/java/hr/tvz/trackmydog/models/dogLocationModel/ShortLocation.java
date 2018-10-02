package hr.tvz.trackmydog.models.dogLocationModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class ShortLocation implements Serializable {

    private int altitude;
    private int bearing;
    private double latitude;
    private double longitude;
    private double speed;
    private long time;

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        String str = "";

        str += " - altitude: " + altitude;
        str += " - bearing: " + bearing;
        str += " - latitude: " + latitude;
        str += " - longitude: " + longitude;
        str += " - speed: " + speed;
        str += " - time: " + time;

        return str;
    }
}
