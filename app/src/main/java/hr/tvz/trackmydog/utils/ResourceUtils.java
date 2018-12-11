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

    /* NEW FUNCTIONS */

    private static String COLOR_TEXT_SUFFIX = "_text";
    private static String COLOR_OPAQUE_SUFFIX = "_op";

    // get any color from resources:
    public static int getAnyColor(String color, Context context) {
        Log.d(TAG, "get color from resources: " + color);
        return context.getResources().getColor(context.getResources().getIdentifier(
                color, "color", context.getPackageName()), null);
    }

    public static int getDogColor(String color, Context context) {
        color = getExistingColor(color);
        int dogColor = context.getResources().getIdentifier(color, "color", context.getPackageName());
        return context.getResources().getColor(dogColor, null);
    }

    // get text color for that dog:
    public static int getTextColor(String color, Context context) {
        Log.d(TAG, "get color from resources: " + color);
        color = getExistingColor(color);
        color += COLOR_TEXT_SUFFIX;

        return context.getResources().getColor(context.getResources().getIdentifier(color, "color",
                context.getPackageName()), null);
    }

    // get opaque color for the dog:
    public static int getOpaqueColor(String color, Context context) {
        Log.d(TAG, "get color from resources: " + color);
        color = getExistingColor(color);
        color += COLOR_OPAQUE_SUFFIX;

        return context.getResources().getColor(context.getResources().getIdentifier(color, "color",
                context.getPackageName()), null);
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


    /**
     * Converts dp to pixels.
     *
     * @param dp - number that needs to be converted
     * @param scale - density of the screen
     * @return - number of pixels that it presents
     */
    // convert dp to pixels:
    // density = number of dps that needs
    public static int convertDpToPixels(float dp, float scale) {
        // TODO - remove to resource utils:
        // TODO - (+0.5) is used instead of round() when casting to int:
        // https://stackoverflow.com/questions/9685658/add-padding-on-view-programmatically

        // TODO - scale from context:
        // float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}