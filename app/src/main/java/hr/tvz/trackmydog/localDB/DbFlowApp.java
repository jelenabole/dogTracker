package hr.tvz.trackmydog.localDB;

import android.app.Application;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import hr.tvz.trackmydog.firebaseWait.MyCallback;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class DbFlowApp extends Application {

    // user iz lokalne baze, na pocetku
    User localUser = null;
    // iz Firebase-a:
    CurrentUser user = null;

    // user DB (FB) reference:
    DatabaseReference userRef = null;

    // if there is no user, give option to add one
    // .. check FB if it already exists, or add new one
    // .. on logout, set user as !active
    // .. logged in user is active

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
        FlowManager.init(new FlowConfig.Builder(this).build());

        // TODO - dohvatiti podatke iz baze:
        // getCurrentUser();
        // addUser();

        System.out.println(" ** DbFlowApp - Get current user");
        // TODO - log all users and save current one
        getAllLocalUsers();
    }

    // TODO - (delete) check all current users:
    // saving user to global variable
    public void getAllLocalUsers() {
        List<User> users = SQLite.select().from(User.class).queryList();
        for (User user : users) {
            System.out.println(user);
            if (user.isActive()) {
                localUser = user;
            }
        }
    }

    public User getMyUser() {
        System.out.println(" ** DbFlowApp - Get user");
        System.out.println(localUser);
        return localUser;
    }


    /* FIREBASE */

    protected void setListenerToCurrentUser(DatabaseReference userRef) {
        System.out.println("get the USER info: " + user.getKey());

        if (localUser.getKey() != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users/" + localUser.getKey());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // System.out.println(dataSnapshot);

                    // TODO - namapirati tog usera na svaku promjenu? potrebno??
                    user = dataSnapshot.getValue(CurrentUser.class);
                    user.setKey(dataSnapshot.getKey());

                    System.out.println(user);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public CurrentUser getFirebaseUser() {
        System.out.println(" ** DbFlowApp - Get current FB user");
        System.out.println(user);
        return user;
    }

    // TODO - firebase user - wait for callback:
    public void setListenerToCurrentUserWithCallback(final MyCallback myCallback) {

        // TODO - local user = ne postoji kod prvog ulogiravanja u aplikaciju
        // postaviti usera prilikom logina, i maknuti za logout

        // TODO - get user (firebase) key:
        if (localUser.getKey() != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users/" + localUser.getKey());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // System.out.println(dataSnapshot);

                    // TODO - namapirati tog usera na svaku promjenu? potrebno??
                    user = dataSnapshot.getValue(CurrentUser.class);
                    user.setKey(dataSnapshot.getKey());

                    System.out.println(user);
                    myCallback.onCallback(user.getDisplayName());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public DatabaseReference getUserReference() {
        return userRef;
    }













    // TODO - firebase user - wait for callback:
    public void readData(final MyCallback myCallback) {
        // TODO - get user (firebase) key:
        String key = localUser.getKey();

        FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/displayName", key))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                myCallback.onCallback(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }










    // TODO - not needed ???
    private CurrentUser currentUserFB;


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

    public void addUser(final String userEmail) {
        System.out.println("ADD NEW USER");
        // get user info, and save it as local user:
        // .. put user in "current user FB"

        FirebaseDatabase.getInstance().getReference("users")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean found = false;
                    for (DataSnapshot userSnaps : dataSnapshot.getChildren()) {
                        // TODO - find user by code:
                        if (userSnaps.child("email").getValue().equals(userEmail)) {
                            // TODO - additional - make user:
                            currentUserFB = userSnaps.getValue(CurrentUser.class);
                            currentUserFB.setKey(userSnaps.getKey());
                            break;
                        }
                    }

                    User user = new User();
                    System.out.println("NEW USER ***");
                    System.out.println(currentUserFB);

                    user.setActive(true);

                    user.setCode("123");
                    user.setDisplayName("jelena");
                    user.setEmail("jelenabole@gmail.com");
                    user.setKey("user123");

                    user.save();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }


    // TODO - find user by email (single event):
    // TODO - not needed ??? (needed for check during new logins)
    protected void findUserByEmail(String email) {
        final String userEmail = email;

        FirebaseDatabase.getInstance().getReference("users")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    boolean found = false;
                    for (DataSnapshot userSnaps : dataSnapshot.getChildren()) {
                        // TODO - find user by code:
                        if (userSnaps.child("email").getValue().equals(userEmail)) {
                            // TODO - additional - make user:
                            currentUserFB = userSnaps.getValue(CurrentUser.class);
                            currentUserFB.setKey(userSnaps.getKey());
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    public CurrentUser getCurrentUserFB() {
        return currentUserFB;
    }
}
