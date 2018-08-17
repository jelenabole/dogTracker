package hr.tvz.trackmydog.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.R;

public class MapRangeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Dog Details Activity";

    @BindView(R.id.address) EditText address;
    @BindView(R.id.searchButton) ImageButton searchButton;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create");

        setContentView(R.layout.activity_add_user_location);
        ButterKnife.bind(this);

        // needed to call onMapReady:
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // add location (safe zone) button listener:
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                Log.d(TAG, "hide keyboard and search");

                // hide keyboard:
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                    // when keyboard is not opened - ignore
                }

                findPlaceOnMap(address.getText().toString());
            }
        });
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "on Map Ready");

        map = googleMap;

        // remove tilt and set max zoom level:
        map.getUiSettings().setRotateGesturesEnabled(false);

        // returns -1 when there's no permission
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != -1) {
            Log.d(TAG, "location permission enabled - set user location");
            // map.setMyLocationEnabled(true);

            // TODO - get current user location
            // builder.include() = user location
            Log.d(TAG, "Include user location");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager;
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.animateCamera(CameraUpdateFactory
                            .newLatLng(userLocation));
                }
            }

        }
    }


    private void findPlaceOnMap(String addressName) {
        if (addressName == null || addressName.equals("")) {
            return;
        }
        Log.d(TAG, "search for address: " + addressName);

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try
        {
            List<Address> addresses = geoCoder.getFromLocationName(addressName, 5);
            if (addresses.size() > 0) {
                Double lat = (double) addresses.get(0).getLatitude();
                Double lon = (double) addresses.get(0).getLongitude();

                Log.d(TAG, "lat-long: " + lat + "......." + lon);
                final LatLng mark = new LatLng(lat, lon);

                Marker marker = map.addMarker(new MarkerOptions()
                        .position(mark)
                        .title(addressName));

                // Move the camera instantly to hamburg with a zoom of 15.
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(mark, 15));
                // Zoom in, animating the camera.
                // map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
