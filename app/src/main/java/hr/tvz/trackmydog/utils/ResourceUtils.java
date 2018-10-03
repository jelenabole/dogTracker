package hr.tvz.trackmydog.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import hr.tvz.trackmydog.R;

// TODO - default color is black = check this for paws and colors (border, text)
public class ResourceUtils {

    private static final String TAG = "Resource Utils";

    private static final String FALLBACK_COLOR = "black";

    // get paw icon (map marker) for the dogs:
    public static int getPawMarker(String color, Resources res, Context context) {
        // blue:    #007EAC        rgb(0, 126, 172)
        // yellow:  #F48F00        rgb(244, 143, 0)
        // red:     #C8492F        rgb(200, 73, 47)
        // green:   #31AA50        rgb(49, 170, 80)
        if (color == null) {
            color = FALLBACK_COLOR;
        }

        switch (color) {
            case "blue":
            case "yellow":
            case "red":
            case "green":
                return res.getIdentifier("paw_" + color, "drawable", context.getPackageName());
            default:
                return res.getIdentifier("paw", "drawable", context.getPackageName());
        }
    }

    // get dot icon (track marker) for the dogs:
    public static int getDotMarker(String color, Resources res, Context context) {
        if (color == null) {
            color = FALLBACK_COLOR;
        }

        switch (color) {
            case "blue":
            case "yellow":
            case "red":
            case "green":
                return res.getIdentifier("dot_" + color, "drawable", context.getPackageName());
            default:
                return res.getIdentifier("dot", "drawable", context.getPackageName());
        }
    }

    // get drawable icon (e.g., home marker):
    public static int getDrawableIcon(String name, Resources res, Context context) {
        return res.getIdentifier(name, "drawable", context.getPackageName());
    }


    // get string from resources (dynamic translations):
    public static String getTextFromResource(String name, Resources res, Context context) {
        return res.getString(res.getIdentifier(name, "string", context.getPackageName()));
    }


    /* GET COLOR FROM STRING NAME */

    // if the color is null or undefined value
    private static String getExistingColor(String color) {
        if (color == null) {
            return FALLBACK_COLOR;
        }

        switch (color) {
            case "yellow":
            case "green":
            case "red":
            case "blue":
                return color;
            default:
                return FALLBACK_COLOR;
        }
    }


    // get dog color:
    // used: DogDetailsActivity
    public static int getColorFromRes(String color, String additional, Resources res, Context context) {
        Log.d(TAG, "get color from resources: " + color);
        color = getExistingColor(color);

        // additional info about color (opaque, text, ...)
        if (additional != null) {
            color += additional;
        }
        return res.getColor(res.getIdentifier(color, "color",
                context.getPackageName()), context.getTheme());
    }

    // get dog color:
    // TODO - function for DogsFragment:
    public static int getDogColor(String color, Resources res, Context context) {
        // TODO - check color:
        // TODO - maybe wrong color - check the resources for colors (make enum)
        color = getExistingColor(color);

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

}