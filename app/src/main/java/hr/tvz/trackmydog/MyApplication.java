package hr.tvz.trackmydog;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import hr.tvz.trackmydog.services.NotificationService;

public class MyApplication extends Application {

    private static final String TAG = "Application";

    private static MyApplication instance;
    
    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " *** MyApplication - on create");

        // TODO - check if needed (??)
        instance = this;

        Fresco.initialize(getApplicationContext());
        FlowManager.init(new FlowConfig.Builder(this).build());

        // prepare app token
        NotificationService.retriveLocalToken();
    }

}
