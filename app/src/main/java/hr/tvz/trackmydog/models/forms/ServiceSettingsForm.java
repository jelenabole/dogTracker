package hr.tvz.trackmydog.models.forms;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ServiceSettingsForm implements Serializable {

    private int interval;
    private boolean started;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("interval", interval);
        result.put("started", started);

        return result;
    }

    @Override
    public String toString() {
        String str = "";

        str += "\n " + "interval: " + interval;
        str += " - " + "started: " + started;

        return str;
    }
}