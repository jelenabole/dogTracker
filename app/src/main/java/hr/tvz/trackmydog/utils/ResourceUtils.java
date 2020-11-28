package hr.tvz.trackmydog.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hr.tvz.trackmydog.R;

// default color is black
public class ResourceUtils {

    private static final String TAG = "Resource Utils";

    private static final String FALLBACK_COLOR = "black";

    // get color resource ID from name:
    public static int getColorResource(String colorName) {
        int color = R.color.grey;
        if (colorName ==  null) return color;
        switch (colorName) {
            case "blue":
                color = R.color.blue; break;
            case "yellow":
                color = R.color.yellow; break;
            case "red":
                color = R.color.red; break;
            case "green":
                color = R.color.green; break;
            case "black":
                color = R.color.black; break;
        }

        return color;
    }

    // get dot icon (track marker) for the dogs in specific color (default grey)
    public static BitmapDescriptor getDogMarkerIcon(String colorName, int icon, Context context) {
        int color = getColorResource(colorName);

        Drawable mDrawable = Objects.requireNonNull(ContextCompat.getDrawable(context, icon)).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(color, null), PorterDuff.Mode.SRC_IN));

        return getMarkerFromIcon(mDrawable);
    }

    private static BitmapDescriptor getMarkerFromIcon(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
            case "gray":
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
    public static int convertDpToPixels(float dp, float scale) {
        // https://stackoverflow.com/questions/9685658/add-padding-on-view-programmatically

        // float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}