package hr.tvz.trackmydog;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hr.tvz.trackmydog.dogModel.CurrentLocation;

public class HelperClass {

    private static final String TAG = "Helper Class";

    // get paw icon (map marker) for the dogs:
    public static int getPawMarker(String color, Resources res, Context context) {
        // blue:    #007EAC        rgb(0, 126, 172)
        // yellow:  #F48F00        rgb(244, 143, 0)
        // red:     #C8492F        rgb(200, 73, 47)
        // green:   #31AA50        rgb(49, 170, 80)
        switch (color) {
            case "blue":
                return res.getIdentifier("paw_blue", "drawable", context.getPackageName());
            case "yellow":
                return res.getIdentifier("paw_yellow", "drawable", context.getPackageName());
            case "red":
                return res.getIdentifier("paw_red", "drawable", context.getPackageName());
            case "green":
                return res.getIdentifier("paw_green", "drawable", context.getPackageName());
                // return res.getIdentifier("paw_" + color, "drawable", context.getPackageName());
            default:
                return res.getIdentifier("paw", "drawable", context.getPackageName());
        }
    }

    // get dog color:
    public static int getColorFromRes(String color, Resources res, Context context) {
        Log.d(TAG, "get color from resources: " + color);
        return res.getColor(res.getIdentifier(color, "color",
                context.getPackageName()), context.getTheme());
    }

    // get dog color:
    // TODO - function for DogsFragment:
    public static int getDogColor(String color, Resources res, Context context) {
        int dogColor = res.getIdentifier(color, "color", context.getPackageName());

        return res.getColor(dogColor, null);
    }



    // get default pictures for dogs (if there are no dog pics)
    public static List<Integer> getDefaultDogPictures() {
        List<Integer> imgList = new ArrayList<>();
        imgList.add(R.drawable.dog2);
        imgList.add(R.drawable.dog1);
        imgList.add(R.drawable.dog3);
        imgList.add(R.drawable.dog4);
        imgList.add(R.drawable.dog5);

        return imgList;
    }

    // get default pictures for dogs (if there are no dog pics)
    public static String getLastLocationTime(CurrentLocation location) {
        if (location == null) {
            return "no location detected";
        } else {
            return converTimeToReadable(location.getTime());
            // new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(location.getTime()));
        }
    }

    // convert date to be user friendly
    // now, today, yesterday, day of the (last) week, or date
    public static String converTimeToReadable(long time) {
        // check if its in the last minute:
        long diff = (new Date()).getTime() - time;
        System.out.println("time difference: " + diff);
        System.out.println("minutes: " + (diff / 60000));
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

            // TODO - delete this - checks:
            System.out.println("Current date: " + currentDay);
            System.out.println("Dog date: " + lastLocationDay);
            System.out.println("Difference : " + daysDifference);

            // ispis:
            str += getNameOfTheDay(daysDifference, lastLocationDay);

        } catch (ParseException ex) {
            // if something fails, just write the date
            str += new SimpleDateFormat("dd.MM.yyyy").format(date);
        }

        // TODO - example formats:
        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 2013


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

    /* CHECK EMPTY STRINGS FOR LABELS */

    // for strings in labels (user / dog detail page):
    public static String getAsStringLabel(String str) {
        return str == null ? "-- unknown --" : str;
    }

    // for strings in labels (user / dog detail page) - for int:
    public static String getAsStringLabel(Integer str) {
        return str == null ? "-- unknown --" : str.toString();
    }

}
