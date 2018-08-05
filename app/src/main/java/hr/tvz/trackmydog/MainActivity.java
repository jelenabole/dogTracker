package hr.tvz.trackmydog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;

import butterknife.ButterKnife;
import hr.tvz.trackmydog.firebaseWait.MyCallback;
import hr.tvz.trackmydog.fragments.DogsFragment;
import hr.tvz.trackmydog.fragments.MapFragment;
import hr.tvz.trackmydog.fragments.ProfileFragment;
import hr.tvz.trackmydog.localDB.DbFlowApp;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class MainActivity extends AppCompatActivity {

    // TODO - info from DbFlowApp:
    private DatabaseReference userRef;
    private CurrentUser user = null;

    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Main - On Create");
        // TODO - need to initialize Fresco before the contectView and using it:
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // TODO - check if we have GPS provider:
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            System.out.println("DOESNT HAVE GPS PROVIDER");
        }

        // Check whether this app has access to the location permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // If the location permission has been granted, then start the TrackerService
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // TODO - dont do nothing (except later show current user location (map fragment)
        } else {
            // If the app doesn’t currently have access to the user’s location, then request access
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }



        // TODO - find the user by email (from local)
        // nakon toga (callback) pozvati fragment

        // set listener to current user
        // do this:
        // code is up in callback

        // TODO - (error) get user from FB in Application:
        System.out.println("KORISNIK check - CALLBACK");
        System.out.println(userRef);
        ((DbFlowApp) getApplication()).setListenerToCurrentUserWithCallback(new MyCallback() {
            @Override
            public void onCallback(String value) {
                System.out.println("CALLBACK **** !1");
                System.out.println(value);

                // TODO - when finished, get userReference:
                userRef = ((DbFlowApp) getApplication()).getUserReference();
                System.out.println("KORISNIK check - CALLBACK - drugi (FB link)");
                System.out.println(userRef);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, MapFragment.newInstance());
                // TODO - error = Cannot perform this action after onSaveInstanceState
                transaction.commit();
            }
        });

        // Set the bottom navigation and onclick listener:
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // TODO - call different activities / fragments:
                    selectedFragment = MapFragment.newInstance();
                    // startActivity(new Intent(getApplicationContext(), QuizListActivity.class));
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = DogsFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = ProfileFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }
    };

}
