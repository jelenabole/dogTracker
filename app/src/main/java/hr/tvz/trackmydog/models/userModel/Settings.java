package hr.tvz.trackmydog.models.userModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Settings implements Serializable {

    private int interval;
    private int fastestInterval;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int isFastestInterval() {
        return fastestInterval;
    }

    public void setFastestInterval(int fastestInterval) {
        this.fastestInterval = fastestInterval;
    }


    @Override
    public String toString() {
        String str = "";

        str += "\n " + "interval: " + interval;
        str += " - " + "fastestInterval: " + fastestInterval;

        return str;
    }
}
