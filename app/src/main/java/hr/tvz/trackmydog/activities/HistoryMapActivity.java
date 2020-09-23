package hr.tvz.trackmydog.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.dogLocationModel.Run;
import hr.tvz.trackmydog.models.dogLocationModel.Tracks;
import hr.tvz.trackmydog.utils.ResourceUtils;
import hr.tvz.trackmydog.utils.TimeDistanceUtils;

public class HistoryMapActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "History Map Activity";

    // model:
    private List<Tracks> dogTracks = new ArrayList<>();
    DatabaseReference tracksRef;
    BitmapDescriptor defaultDot;
    BitmapDescriptor colorDot;
    BitmapDescriptor marker;

    @BindView(R.id.dateStart) TextView dateStartLabel;
    @BindView(R.id.dateEnd) TextView dateEndLabel;
    @BindView(R.id.time) TextView timeLabel;
    @BindView(R.id.distance) TextView distanceLabel;

    Run run;
    String runID;
    String dogKey;
    int color;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create");

        // get index of a dog:
        runID = getIntent().getStringExtra("runID");
        dogKey = getIntent().getStringExtra("dogKey");
        String dogColor = getIntent().getStringExtra("dogColor");

        // get dog color and marker icon
        color = ResourceUtils.getDogColor(dogColor, getBaseContext());
        defaultDot = BitmapDescriptorFactory.fromResource(ResourceUtils
                .getDotMarker("gray", getBaseContext().getResources(), getBaseContext()));
        colorDot = BitmapDescriptorFactory.fromResource(ResourceUtils
                .getDotMarker(dogColor, getBaseContext().getResources(), getBaseContext()));
        marker = BitmapDescriptorFactory.fromResource(ResourceUtils
                .getPawMarker(dogColor, getBaseContext().getResources(), getBaseContext()));
        context = getBaseContext();

        tracksRef = FirebaseDatabase.getInstance().getReference("dogs/" + dogKey + "/tracks/" + runID);

        setContentView(R.layout.activity_history_map);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // style map:
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.empty_map_silver));

        // get dog's route info
        tracksRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "get dog tracks");

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            dogTracks.add(snap.getValue(Tracks.class));
                        }

                        long start = 0;
                        long end = 0;
                        double distance = 0;

                        // go throught all dog tracks:
                        LatLng previous = null;
                        List<LatLng> polydots = new ArrayList<>();
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (int i = 0; i < dogTracks.size(); i++) {
                            LatLng position = new LatLng(dogTracks.get(i).getLatitude(), dogTracks.get(i).getLongitude());
                            polydots.add(position);
                            builder.include(position);

                            // add marker
                            BitmapDescriptor dogIcon;
                            float anc = 1;
                            if (previous == null) {
                                dogIcon = colorDot;
                                anc = 0.5f;
                                start = dogTracks.get(i).getTime();
                            } else if (i == dogTracks.size() - 1) {
                                dogIcon = marker;
                                end = dogTracks.get(i).getTime();
                            } else {
                                dogIcon = defaultDot;
                                anc = 0.5f;
                            }

                            googleMap.addMarker(new MarkerOptions()
                                    .icon(dogIcon).position(position)
                                    .draggable(false).anchor(0.5f, anc)
                            );

                            // calculate distance:
                            if (previous != null) {
                                distance += TimeDistanceUtils.distanceBetweenTwoGeoPoints(previous, position);
                            }
                            previous = position;
                        }

                        // if end is not the last track
                        if (end == 0) end = start;
                        createRun(start, end, end - start, distance);
                        // saveRun();

                        googleMap.addPolyline(new PolylineOptions()
                                .clickable(false).addAll(polydots)
                                .width(2f).color(Color.GRAY).geodesic(true)
                        );

                        // set camera view
                        int padding = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.10);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
                        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(previous.getLatitude(), previous.getLongitude()), 18));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error - get dog's route (FB): " + databaseError.getCode());
                    }
                });
    }

    private void createRun(long start, long end, long time, double distance) {
        run = new Run(TimeDistanceUtils.convertTimestampToReadable(start),
                TimeDistanceUtils.convertTimestampToReadable(end),
                time / 1000, distance);

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        dateStartLabel.setText(run.getStarted());
        dateEndLabel.setText(run.getEnded());
        timeLabel.setText(formatter.format(time));
        distanceLabel.setText(TimeDistanceUtils.formatFloatNumber(run.getDistance(), 1) + " m");
    }

    private void saveRun() {
        FirebaseDatabase.getInstance().getReference("dogs/" + dogKey + "/runs/" + runID).setValue(run);
    }

}
