package hr.tvz.trackmydog.models.forms;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ServiceSettingsForm implements Serializable {

    private int interval;
    private boolean start;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("interval", interval);
        result.put("start", start);

        return result;
    }

    @Override
    public String toString() {
        String str = "";

        str += "\n " + "interval: " + interval;
        str += " - " + "start: " + start;

        return str;
    }
}