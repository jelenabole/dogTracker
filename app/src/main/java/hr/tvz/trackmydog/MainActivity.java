package hr.tvz.trackmydog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.activities.UserDetailsAddActivity;
import hr.tvz.trackmydog.firebaseServices.FBAuth;
import hr.tvz.trackmydog.fragments.DogsFragment;
import hr.tvz.trackmydog.fragments.MapFragment;
import hr.tvz.trackmydog.fragments.ProfileFragment;

public class MainActivity extends BaseActivity {

    private static final String TAG = "Main Activity";
    private static final int PERMISSIONS_REQUEST = 100;

    @BindView(R.id.navigation) BottomNavigationView navigation;

    @Override
    protected void onResume() {
        // starts also after create view:
        super.onResume();
        Log.d(TAG, "on Resume - hide loading dialog");
        hideProgressDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on Create");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // check if user has filled data - or start add user activity:
        // TODO - open the add user info activity
        /*
        if (FBAuth.getCurrentUserFB().getName() == null) {
            startActivity(new Intent(MainActivity.this, UserDetailsAddActivity.class));
        }
        */

        // TODO - check if we have GPS provider:
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e(TAG, "Doesn't have GPS provider.");
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

        // set the first fragment (map fragment):
        // TODO - on refresh/change orientation = remember current fragment
        // if savedInstance exists, don't inflate the beginning fragment (already exists):
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, MapFragment.newInstance()).commit();
        }

        // Set the bottom navigation and onclick listener:
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // call different fragments:
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    selectedFragment = MapFragment.newInstance();
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
