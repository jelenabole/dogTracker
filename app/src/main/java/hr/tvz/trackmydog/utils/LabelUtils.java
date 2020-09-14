package hr.tvz.trackmydog.utils;

import java.util.Date;

public class LabelUtils {

    private static final String TAG = "Label Utils";

    /* CHECK EMPTY STRINGS FOR LABELS */

    private static String DEFAULT_EMPTY = " --- ";

    // for strings in labels (user / dog detail page):
    public static String getAsStringLabel(String str) {
        return str == null ? DEFAULT_EMPTY: str;
    }

    // for strings in labels (user / dog detail page) - for int:
    public static String getAsStringLabel(Integer str) {
        return str == null ? DEFAULT_EMPTY : str.toString();
    }


    /* STRINGS FOR EDIT FIELDS
        used: DogDetailsEditActivity
    */

    public static String getStringForEdit(String str) {
        return str == null ? "" : str;
    }

    public static String getStringForEdit(Integer str) {
        return str == null ? "" : str.toString();
    }

    public static String getStringForEdit(Date str) {
        return str == null ? "" : str.toString();
    }

    // check if field is empty
    // used in AddNewDog (deleted), and EditDog
    public static boolean isFieldEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }



    /* Add new dog = check text fields */

    // return new string or null
    // Add new dog = return string or null
    // used: Add new user / edit
    public static String getTextOrNull(String str) {
        if (str != null && !str.equals("")) {
            return str;
        }
        return null;
    }

    // Add new dog = return integer or null
    public static Integer getIntegerOrNull(String str) {
        if (str != null && !str.equals("")) {
            // pretvoti u broj i vrati broj
            return Integer.valueOf(str);
        }
        return null;
    }

}
