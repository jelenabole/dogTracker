package hr.tvz.trackmydog.utils;

import android.text.format.DateFormat;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import hr.tvz.trackmydog.models.dogLocationModel.ShortLocation;

public class TimeDistanceUtils {

    private static final String TAG = "Time Utils";

    // for history routes and runs
    public static String convertTimestampToReadable(long timestamp) {
        java.text.DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatted = formatter.format(timestamp);

        return dateFormatted;
    }

    public static String formatFloatNumber(double number, int places) {
        if (number == 0) return "0";

        // return String.format("%.1f", number);
        return new BigDecimal(number).setScale(places, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static double distanceBetweenTwoGeoPoints(LatLng previous, LatLng next) {
        double earthRadius = 6371e3; // earth's radius in metres

        // values in radians
        double prevLat = previous.latitude * Math.PI / 180;
        double prevLong = previous.longitude * Math.PI / 180;
        double nextLat = next.latitude * Math.PI / 180;
        double nextLong = next.longitude * Math.PI / 180;

        // approximation
        double x = (nextLong - prevLong) * Math.cos((nextLat + prevLat) / 2);
        double y = (nextLat - prevLat);
        return Math.sqrt(x*x + y*y) * earthRadius;
    }

    // get default pictures for dogs (if there are no dog pics)
    public static String getLastLocationTime(ShortLocation location) {
        if (location == null) {
            return "no location detected";
        } else {
            return converTimeToReadable(location.getTime());
            // new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(location.getTime()));
        }
    }


    /* functions used when changingLocationOfDog - to check markers */

    private static long getCurrentTime() {
        return (new Date()).getTime();
    }

    // in miliseconds:
    public static long differenceBetweenCurrentTime(long time) {
        return getCurrentTime() - time;
    }



    public static long differenceBetweenCurrentTimeInHours(long time) {
        long diff = (new Date()).getTime() - time;

        return diff / 3600000;
    }


    /* Converting time to units */

    public static long convertToHours(long time) {
        return time / 3600000;
    }

    public static long convertToMinutes(long time) {
        return time / 60000;
    }

    public static long convertToSeconds(long time) {
        return time / 1000;
    }


    /* OTHER FUNCTIONS ..... ?? */

    // convert date to be user friendly
    // now, today, yesterday, day of the (last) week, or date
    public static String converTimeToReadable(long time) {
        // check if its in the last minute:
        long diff = (new Date()).getTime() - time;
        // TimeUnit.MILLISECONDS.toMinutes(diff)

        if (diff / 60000 < 1) {
            return "now";
        }

        // if its more then a minute, then write time
        String str = "";
        Date date = new Date(time);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDay = sdf.parse(sdf.format(new Date()));
            Date lastLocationDay = sdf.parse(sdf.format(time));
            long daysDifference = TimeUnit.MILLISECONDS
                    .toDays(currentDay.getTime() - lastLocationDay.getTime());

            // ispis:
            str += getNameOfTheDay(daysDifference, lastLocationDay);

        } catch (ParseException ex) {
            // if something fails, just write the date
            str += new SimpleDateFormat("dd.MM.yyyy").format(date);
        }

        /*
        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 2013
        */

        str += ", ";
        str += new SimpleDateFormat("HH:mm").format(date);

        return str;
    }

    // get formatted day:
    private static String getNameOfTheDay(long daysDifference, Date date) {
        // returns today, yesterday, day of the last week, or date:
        if (daysDifference == 0) {
            return "today";
        } else if (daysDifference == 1) {
            return "yesterday";
        } else if (daysDifference < 7) {
            return (String) DateFormat.format("EEEE", date);
        } else {
            return new SimpleDateFormat("dd.MM.yyyy").format(date);
        }
    }

}
