package hr.tvz.trackmydog.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import hr.tvz.trackmydog.localDB.Token;

// TODO - token is same for multiple users on the same device
// TODO - in case the local token is not defined = maybe wait during sign in process
public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "FB Notification Service";

    private static String token;

    // get current app token
    public static String getAppToken() {
        return token;
    }

    // call at the beginning of the app to get local token:
    public static void retriveLocalToken() {
        Log.d(TAG, "Retrieve token from local DB - at app start");
        Token tok = SQLite.select().from(Token.class).querySingle();
        if (tok != null) {
            token = tok.getToken();
        }
    }

    // TODO - instalirat ovo na usera :D
    // get token - if there are differences on user/locally
    public static void retrieveApplicationToken() {
        // TODO - token is changed when ...
        // .. app deletes it's instance ID, when it is restored on new device,
        // .. when user uninstalls/reinstalls the app, or cleans app data

        // Local token (or null)
        final String localToken = token;
        Log.e(TAG, "Local token: " + localToken);

        // TODO - receive token from firebase, check if its the same as local
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                // check current app token
                String fbToken = instanceIdResult.getToken();
                Log.w(TAG, "Application token check: \n \t" + fbToken);
                Log.w(TAG, "Local token: \n \t" + token);

                // TODO - token shouldnt be empty (ever) and they should always match
                if (token == null) {
                    // TODO - new token does this:
                    // saveTokenLocally(fbToken);
                } else if (!fbToken.equals(token)) {
                    Log.e("ERROR", " - token is not the same as local");
                    // TODO - override the local one (?)
                } else {
                    Log.d(TAG, "Token is the same as the local one");
                }
                // TODO - ako postoji razlika, promijeni u firebaseu (??)
                // TODO - test user with wrong token
            }
        });
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        Log.e(TAG, " *** NEW TOKEN: " + newToken);
        token = newToken;

        // TODO - save new token locally:
        saveTokenLocally(newToken);
    }

    private static void saveTokenLocally(String newToken) {
        Log.d(TAG, "save token locally: " + newToken);

        Token token = new Token();
        token.setId(0);
        token.setToken(newToken);
        token.save();
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

        // Check if message contains a notification payload (shouldnt be null):
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, " *** Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, " *** Message Notification Body: " + remoteMessage.getNotification().getBody());

            // show message to user:
            showLongToast(this, remoteMessage.getNotification().getBody());
        }
    }

    // send toast messages from anywhere
    public static void showLongToast(final Context context, final String message) {
        // can't toast on a thread that has not called Looper.prepare()
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // TODO - check with context (this in service)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                // Toast.makeText(MyApplication.getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
