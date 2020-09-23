package hr.tvz.trackmydog.models.dogLocationModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Run implements Serializable {

    private String started;
    private String ended;
    private double duration; // in seconds
    private double distance; // in meters

    private long timestamp;
    private String key;

    public Run() {}

    public Run(String started, String ended, long duration, double distance) {
        this.started = started;
        this.ended = ended;
        this.duration = duration;
        this.distance = distance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        String str = "";

        str += " - " + "started: " + started;
        str += " - " + "longitude: " + ended;
        str += " - " + "distance: " + distance;
        str += " - " + "duration: " + duration;

        return str;
    }
}
