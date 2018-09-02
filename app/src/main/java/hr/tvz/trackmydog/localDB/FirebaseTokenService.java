package hr.tvz.trackmydog.localDB;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseTokenService extends FirebaseMessagingService {

    private static final String TAG = "FB Messaging Service";
    private static String token;

    // TODO - token should be deleted when user is signed out
    // get token when changing the user (sign in / register)
    public static String getAppToken() {
        return token;
    }

    // called at the app start
    // get app token when its started:
    public static void retrieveApplicationToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                Log.e(TAG, "Application token: " + token);
            }
        });
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        Log.d(TAG, " *** NEW TOKEN: " + newToken);

        token = newToken;
        // save token on server, for notifications
        sendRegistrationToServer(newToken);
    }

    private void sendRegistrationToServer(String newToken) {
        // TODO - send reg token to firebase
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, " *** Message from: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, " *** Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, " *** Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

}
