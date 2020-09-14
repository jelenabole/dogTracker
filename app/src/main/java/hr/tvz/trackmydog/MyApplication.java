package hr.tvz.trackmydog;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import hr.tvz.trackmydog.firebaseServices.TokenService;

public class MyApplication extends Application {

    private static final String TAG = "Application";

    private static String userKey;
    private static MyApplication instance;

    public static String getUserKey() {
        return userKey;
    }
    public static void setUserKey(String userKey) {
        MyApplication.userKey = userKey;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " *** MyApplication - on create");

        instance = this;
        Fresco.initialize(getApplicationContext());
        FlowManager.init(new FlowConfig.Builder(this).build());

        // prepare app token
        TokenService.retriveLocalToken();
    }

}
