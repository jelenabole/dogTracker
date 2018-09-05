package hr.tvz.trackmydog.localDB;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "FB Notification Service";
    private static String token;

    // TODO - token should be deleted when user is signed out
    // get token when changing the user (sign in / register)
    public static String getAppToken() {
        return token;
    }

    // called at the app start
    // get app token when its started:
    public static void retrieveApplicationToken() {
        // TODO - maybe saving it locally, and replace it only on change (??)
        // reg token is changed when app deletes it's instance ID, when it is restored on new device,
        // .. when user uninstalls/reinstalls the app, or cleans app data

        Log.e(TAG, "Token should be shown: " + token);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                Log.e(TAG, "Application token: \n \t" + token);
            }
        });
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        Log.d(TAG, " *** NEW TOKEN: " + newToken);
        token = newToken;

        // save token on server, for notifications
        // TODO - change token on user FB and all dogs
        sendRegistrationToServer(newToken);
    }

    private void sendRegistrationToServer(String newToken) {
        // TODO - send reg token to firebase
        // save token on user (and all of its dogs, after fork)
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, " *** Message received: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, " *** Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, " *** Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

}
