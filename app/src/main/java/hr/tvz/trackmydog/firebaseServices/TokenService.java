package hr.tvz.trackmydog.firebaseServices;

import androidx.annotation.NonNull;
import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.reflect.Field;
import java.util.Map;

import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.localDB.Token;

public class TokenService extends FirebaseMessagingService {

    private static final String TAG = "FB Notification Service";
    private static String token;

    // get current app token
    public static String getAppToken() {
        return token;
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Log.e(TAG, " *** NEW TOKEN: " + newToken);
        token = newToken;

        // save new token locally:
        saveTokenLocally(newToken);
    }

    // call at the beginning of the app to get local token:
    public static void retriveLocalToken() {
        Log.d(TAG, "Retrieve token from local DB - at app start");
        Token tok = SQLite.select().from(Token.class).querySingle();
        if (tok != null) {
            token = tok.getToken();
        }
    }

    private static void saveTokenLocally(String newToken) {
        Log.d(TAG, "save token locally: " + newToken);

        Token token = new Token();
        token.setId(0);
        token.setToken(newToken);
        token.save();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, " *** Message received: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload (shouldnt be null):
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, " *** Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, " *** Message Notification Body: " + remoteMessage.getNotification().getBody());

            // show message to user:
            showSnack(remoteMessage.getNotification().getBody());
            // showLongToast(this, remoteMessage.getNotification().getBody());
        }
    }

    // send snack messages from anywhere
    private void showSnack(final String message) {
        final Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG, "show snack = can't get activity");
            return;
        }

        Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView()
                        .findViewById(android.R.id.content),
                message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("REMOVE", v -> Log.e(TAG, "snackbar should be removed"));
        // change action color:
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        snackbar.show();
    }

    // get current activity from anywhere
    public static Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }

            return null;
        } catch (Exception ex) {
            Log.d(TAG, "Error while getting current activity");
            return null;
        }
    }
}
