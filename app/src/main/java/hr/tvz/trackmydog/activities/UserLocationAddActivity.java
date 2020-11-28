package hr.tvz.trackmydog.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.firebaseServices.UserService;
import hr.tvz.trackmydog.models.forms.SafeZoneForm;
import hr.tvz.trackmydog.utils.ResourceUtils;

public class UserLocationAddActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "User Location Activity";

    // init location - Zagreb:
    private final LatLng initLocation = new LatLng(45.800007, 15.979110);

    @BindView(R.id.error) TextView errorText;

    @BindView(R.id.addressText) EditText addressText;
    @BindView(R.id.searchButton) ImageButton searchButton;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.rangeSeekbar) SeekBar rangeSeekbar;
    @BindView(R.id.rangeText) TextView rangeText;

    private final int rangeStep = 100;

    private GoogleMap map;
    private Marker marker;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create");

        setContentView(R.layout.activity_user_location_add);
        ButterKnife.bind(this);

        // this is needed to call onMapReady:
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null : "User Location map is null";
        mapFragment.getMapAsync(this);

        // add location (safe zone) button listener:
        searchButton.setOnClickListener(v -> {
            Log.d(TAG, "hide keyboard and search");

            String addressName = addressText.getText().toString();
            if (addressName.equals("")) return;

            // hide keyboard:
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null : "System keyboard is null";
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // when keyboard is not opened - ignore
            }

            repositionMarkerByAddress(addressText.getText().toString());
        });

        // add location (safe zone) button listener:
        saveButton.setOnClickListener(v -> {
            Log.d(TAG, "save location and close view (???)");
            saveLocationToUser();
        });

        // seekbar = range
        rangeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // minimum allowed = 1 (100)
                    if (progress < 1) {
                        seekBar.setProgress(1);
                        progress = 1;
                    }
                    // interval = range step (100)
                    String str = (progress * rangeStep) + " m";
                    rangeText.setText(str);

                    // change radius on map:
                    circle.setRadius(progress * rangeStep);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "on Map Ready");
        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_without_transit));

        int height = getResources().getDisplayMetrics().heightPixels;

        int padding = (int) (height * 0.1);
        googleMap.setPadding(0, padding, 0, padding);

        // remove tilt
        map.getUiSettings().setRotateGesturesEnabled(false);
        // disable map toolbar
        map.getUiSettings().setMapToolbarEnabled(false);
        // disable zoom buttons
        map.getUiSettings().setZoomControlsEnabled(false);

        // initialize marker:
        initMarker();

        // set drag listener (to animate camera when marker is moved):
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                refreshMarkerInfo();
            }
            @Override public void onMarkerDragStart(Marker arg0) { }
            @Override public void onMarkerDrag(Marker arg0) { }
        });

        // returns -1 when there's no permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != -1) {
            Log.d(TAG, "location permission enabled (1)");

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "location permission enabled (2) - permission granted");
                LocationManager locationManager;
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                assert locationManager != null : "Location Manager is null";
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Log.d(TAG, "location permission enabled (3) - location exists");
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    repositionMarkerByLocation(userLocation);
                }
            }
        }
    }

    public void zoomToMarker() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
    }

    // init marker on Zagreb
    public void initMarker() {
        int icon = ResourceUtils.getDrawableIcon("users_location", getResources(), this);

        marker = map.addMarker(new MarkerOptions()
                .position(initLocation)
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .title("Zagreb, Croatia")
                .draggable(true)
        );
        marker.showInfoWindow();

        // range circle:
        circle = map.addCircle(new CircleOptions()
                .center(initLocation)
                .radius(100)
                .fillColor(0x30ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2)
        );

        zoomToMarker();
    }

    // change marker title after DRAGGING (find name):
    // title and circle fixes
    public void refreshMarkerInfo() {
        circle.setCenter(marker.getPosition());

        String addressName = getAddressOfLocation();
        marker.setTitle(addressName);
        marker.showInfoWindow();

        zoomToMarker();
        // replace text from search bar:
        addressText.setText("");
        // addressText.setText(addressName);
    }


    /* functions for re-positioning of the marker */

    // change marker location and title (user location, or location search)
    public void repositionMarkerByLocation(LatLng location) {
        Log.d(TAG, "reposition marker - location: "
                + location.latitude + " - " + location.longitude);
        marker.setPosition(location);
        refreshMarkerInfo();
    }

    // change marker location by address name (search bar)
    private void repositionMarkerByAddress(String addressName) {
        Log.d(TAG, "reposition marker - address: " + addressName);

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(addressName, 5);
            if (addresses.size() > 0) {
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();

                Log.d(TAG, "address: "
                        + getAddressOnly(addresses.get(0).getAddressLine(0)));

                repositionMarkerByLocation(new LatLng(lat, lng));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // for dragging marker (changing address name - marker title):
    private String getAddressOfLocation() {
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                return getAddressOnly(addresses.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e(TAG, "Converting location to address unsuccessful");
        }

        return "- not found -";
    }

    // additional function for cropping the address lines:
    public String getAddressOnly(String str) {
        return str.substring(0, str.indexOf(","));
    }

    // save location to user FB and close the activity
    private void saveLocationToUser() {
        errorText.setVisibility(View.GONE);

        SafeZoneForm safeZone = new SafeZoneForm();
        LatLng position = marker.getPosition();

        safeZone.setLocationName(getAddress(position, addressText.getText().toString()));
        safeZone.setLatitude(position.latitude);
        safeZone.setLongitude(position.longitude);
        safeZone.setRange(rangeSeekbar.getProgress() * rangeStep);

        UserService.saveSafeZone(safeZone, error -> {
            if (!error) {
                Log.d(TAG, "safe location added successfully");
                finish();
            } else {
                Log.d(TAG, "error while saving user - DB error");
                errorText.setText(getString(R.string.error_save_to_database));
                errorText.setVisibility(View.VISIBLE);
                hideProgressDialog();
            }
        });
    }


    // fallback = string that the user wrote, in case full address isn't found:
    private String getAddress(LatLng location, String fallbackString) {
        double latitude = location.latitude;
        double longitude = location.longitude;

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                String street = address.getThoroughfare() == null ? "" : address.getThoroughfare();
                street += address.getSubThoroughfare() == null ? "" : " " + address.getSubThoroughfare();
                String city = address.getLocality() == null ? "" : address.getLocality();

                return street + (street.length() != 0 && city.length() != 0 ? ", " : "") + city;
            }
        } catch (IOException e) {
            Log.e(TAG, "Converting location to address unsuccessful");
        }

        // if no address was found, return whatever the user wrote:
        if (fallbackString.length() < 1) {
            return "(location name)";
        }
        return fallbackString;
    }
}
