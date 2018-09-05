package hr.tvz.trackmydog.localDB;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class DbFlowApp extends Application {

    private static final String TAG = "Application";

    private static DbFlowApp instance;
    
    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " *** DbFlowApp - on create");

        // TODO - check if needed (??)
        instance = this;

        Fresco.initialize(getApplicationContext());
        FlowManager.init(new FlowConfig.Builder(this).build());

        // prepare app token
        NotificationService.retriveLocalToken();
    }

}
