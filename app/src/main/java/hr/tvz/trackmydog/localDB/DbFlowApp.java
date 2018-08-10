package hr.tvz.trackmydog.localDB;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class DbFlowApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
        FlowManager.init(new FlowConfig.Builder(this).build());

        System.out.println(" *** DbFlowApp - on create");
        // getAllLocalUsers();
    }

    // TODO - (delete) check all current users:
    // saving user to global variable
    /*
    public void getAllLocalUsers() {
        List<User> users = SQLite.select().from(User.class).queryList();
        for (User user : users) {
            System.out.println(user);
            if (user.isActive()) {
                localUser = user;
            }
        }
    }


    // TODO - logout:
    public void logout() {
        // Local user, active = false; user = null;
        removeActiveUserFlag();

    }

    protected void removeActiveUserFlag() {
        // update active flag as false and delete current user
        localUser.setActive(false);
        localUser.update();
        localUser = null;
    }


    private void addUser() {
        System.out.println("ADD NEW USER");
        User user = new User();

        user.setActive(true);
        user.setCode("123");
        user.setDisplayName("jelena");
        user.setEmail("jelenabole@gmail.com");
        user.setKey("user123");

        user.save();
    }
    */


}
