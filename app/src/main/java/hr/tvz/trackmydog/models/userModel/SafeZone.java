package hr.tvz.trackmydog.models.userModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class SafeZone implements Serializable {

    private String locationName;
    private Double latitude;
    private Double longitude;
    private Integer range;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRange() {
        return range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", locationName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("range", range);

        return result;
    }

    @Override
    public String toString() {
        String str = "********** \n";

        str += " - " + "name: " + locationName;
        str += " - " + "latitude: " + latitude;
        str += " - " + "longitude: " + longitude;
        str += " - " + "range: " + range;

        return str;
    }
}
