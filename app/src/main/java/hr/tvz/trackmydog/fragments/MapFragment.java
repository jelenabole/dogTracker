package hr.tvz.trackmydog.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.dogModel.CustomDogList;
import hr.tvz.trackmydog.dogModel.Dog;
import hr.tvz.trackmydog.newDogModel.DogMarker;
import hr.tvz.trackmydog.newDogModel.Tracks;
import hr.tvz.trackmydog.userModel.BasicDog;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class MapFragment extends ListFragment implements OnMapReadyCallback {

    private static final String TAG = "Map fragment";

    // for tracks listeners:
    DatabaseReference tracksRef;
    ValueEventListener tracksListener;
    private List<Marker> tracks;
    // number of last locations to get from FB:
    int NUMBER_OF_LAST_LOCATIONS = 100;


    boolean followEnabled = true; // if button is pressed, map not touched
    int followDogIndex = -1; // follow all dogs
    final float MAX_ZOOM_LEVEL = 18; // max map zoom level (if dogs are too close, or only one)
    // Zoom level:
    // 1 - world, 5 - continent
    // 10 - city, 15 streets, 20 - buildings (21 - EU,USA, 22-23 max)

    // for setting up markers:
    final int MAX_HOURS_PASSED_FOR_MARKER = 10; // longer not showing
    final int MAX_MIN_BETWEEN_MARKERS = 10; // longer not showing
    final int MAX_NUMBER_OF_LAST_TRACKS = 7; // the ones that are not the same, and not > 10 mins apart
    final float MIN_OPACITY = 0.1f; // important for normalizing time that passed

    @BindView(R.id.dogThumbsLayout) LinearLayout dogThumbsLayout;
    @BindView(R.id.noDogsLayout) LinearLayout noDogsLayout;

    private List<Integer> defaultThumbs;
    private List<Marker> markers; // animal markers
    private CurrentUser user;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    private List<Dog> dogs;
    private GoogleMap map;

    @Override
    // TODO - called on refresh too (changing screen orientation):
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        user = FBAuth.getCurrentUserFB();

        // initialize arrays:
        dogs = new ArrayList<>();
        markers = new ArrayList<>();
        tracks = new ArrayList<>();
        defaultThumbs = HelperClass.getDefaultDogPictures();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);

        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // show dogs if they exist:
        showThumbsIfDogsExist();

        return v;
    }

    /**
     * Checks if user has dogs, and creates their thumbnails on map.
     */
    private void showThumbsIfDogsExist() {
        if (user.getDogs() != null) {
            Log.w(TAG, "user has dogs");
            dogThumbsLayout.setVisibility(View.VISIBLE);
            noDogsLayout.setVisibility(View.GONE);

            getDogsForThumbList();
        } else {
            Log.w(TAG, "user doesn't have dogs");
            dogThumbsLayout.setVisibility(View.GONE);
            noDogsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
    }



    /**
     * Manipulates the map once available. This callback is triggered when the map is ready for use.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "on Map Ready");

        map = googleMap;

        // set map style:
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
        } catch (Exception ex) {
            // possible exceptions = Resources.NotFoundException, no Context, parsing error, ...
            Log.w(TAG, "stlye not added - error: " + ex.getMessage());
        }

        // remove tilt, map toolbar and zoom buttons:
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        // map.getUiSettings().setZoomControlsEnabled(false);

        // TODO - set listener (cause of the zoom and follow):
        // if map is clicked (zoomed or moved) - stop following:
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                // TODO - error - only first gesture needed:
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    // double-tap (zoom) or drag
                    // changes user made = move, double-tap (zoom)
                    Log.w(TAG, "MAP TOUCHED - DISABLE FOLLOW");
                    stopFollowing();
                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_API_ANIMATION) {
                    // changes produced by tapping on marker or double-tapping anywhere
                    Toast.makeText(getActivity(), "The user tapped something on the map.",
                            Toast.LENGTH_SHORT).show();
                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_DEVELOPER_ANIMATION) {
                    // changes produced by app
                }
            }
        });

        // returns -1 when there's no permission
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != -1) {
            Log.d(TAG, "location permission enabled - set user location");
            map.setMyLocationEnabled(true);
        }

        Log.d(TAG, "setting markers for dogs");
        // TODO - get all dogs, and set markers (individual listeners):
        // set dog reference for the marker, or add null (for deleted dog)
        if (user.getDogs() != null) {
            Log.d(TAG, "number of dogs: " + user.getDogs().size());
            for (int i = 0; i < user.getDogs().size(); i++) {
                // TODO - null / dog = if dog check
                if (user.getDogs().get(i) != null) {
                    // TODO - we need dog info (key, index, name) - so sending the whole dog
                    setListenerOnDogLocation(user.getDogs().get(i));
                }
                // TODO - just add null, and change it with marker later:
                markers.add(null);
            }
        }

        // position maps "my location" button bottom-right
        View mapView = this.getView();
        View userLocationButton = ((View) mapView.findViewById(
                Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams buttonLayoutParams =
                (RelativeLayout.LayoutParams) userLocationButton.getLayoutParams();
        buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        buttonLayoutParams.setMargins(0,0,30,30);
    }


    /*  DOGS = get reference and put on the map  */

    // set dog listener (map location)
    // on change, change the marker on the map
    // recalculate the zoom level and position (same as for the user change)
    // TODO - needed more info from the dog (key, index, name) - sending whole dog
    protected void setListenerOnDogLocation(final BasicDog usersDog) {
        FirebaseDatabase.getInstance().getReference("dogs/" + usersDog.getKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // add important info on the dog (index, name, color):
                        DogMarker dog = dataSnapshot.getValue(DogMarker.class);
                        dog.setIndex(usersDog.getIndex());
                        dog.setName(usersDog.getName());
                        dog.setColor(usersDog.getColor());

                        Log.d(TAG + " dog listener", "dog from listener: \n" + dog);
                        // TODO - isAdded = checking for context
                        // ... (on some FB refreshes, fragment isnt added on activity)
                        if (isAdded()) {
                            Log.d(TAG + " dog listener", "isAdded passed");
                            changeMarkerLocation(dog);
                        } else {
                            Log.w(TAG + " dog listener", "isAdded to context failed");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * Changes location of a marker on the map. Called when dog location is changed.
     * If location is null, then marker is removed. Otherwise, marker is either added, or
     * the current one is replaced.
     *
     * There is a time limit (MAX HOURS) for "old" markers - not useful after some time. That is
     * checked only when a new marker is added (map fragment refreshed or dog chip turned on, or FB
     * triggers), cause updates on marker are always current.
     * Marker opacity changes depending on the time that passed from the last location input. On
     * location update its set back to 1.0, while on adding new marker the time passed is normalized
     * based on the MAX_HOURS allowed.
     *
     * ** If dog is refreshed, and its the dog thats been followed, add its tracks (or refresh).
     * ** If the app keeps running, "old" markers won't dissapear (unless map is refreshed).
     * ** check for problems with time zones (between app and chip/firebase time)
     *
     * @param dog - dog info from Firebase (with additional info from user - name, index, color)
     */
    private void changeMarkerLocation(DogMarker dog) {
        int index = dog.getIndex();
        Log.d(TAG, "change marker location - index: " + index);

        if (dog.getLocation() != null) {
            // TODO - check for problems with time zones - get it when registering user:

            // add new marker, or reposition existing one
            if (markers.get(index) == null) {
                // TODO - only check time diff when its new marker (its either new dog / without location..
                // TODO - check the time to decide to add marker:
                // TODO - remove marker ?? - will it ever change so that we need to
                // If first time adding marker: (either chip started-bad, or map frag started-intended):
                long diff = HelperClass.differenceBetweenCurrentTimeInHours(dog.getLocation().getTime());
                Log.d(TAG, "last location before (h): " + diff);
                if (diff > MAX_HOURS_PASSED_FOR_MARKER) {
                    // if longer than 2 hours, dont add marker
                    // TODO - check if anything else is needed
                    return;
                }

                Log.d(TAG + " - change marker", "(new) marker positioned for dog: " + dog.getName());
                LatLng newPosition = new LatLng(dog.getLocation().getLatitude(), dog.getLocation().getLongitude());

                // else - calculate opacity by time - normalize data to 0-1 range:
                // formula => normalized = (diff - min) / (max - min);
                // .. but opacity should go in reverse order = more hours means lower opacity (1-...)
                float opacity = 1f - ((diff - MIN_OPACITY) / (MAX_HOURS_PASSED_FOR_MARKER - MIN_OPACITY));
                Log.e(TAG, "dog - last location time: " + dog.getLocation().getTime());
                Log.d(TAG, "opacity: " + opacity);

                int icon = HelperClass.getPawMarker(dog.getColor(), getResources(), getContext());
                markers.set(index, map.addMarker(new MarkerOptions()
                        .position(newPosition)
                        .icon(BitmapDescriptorFactory.fromResource(icon))
                        .alpha(opacity)
                        .title(dog.getName())
                        .snippet("last updated: " + HelperClass.converTimeToReadable(dog.getLocation().getTime()))
                ));
                // TODO - add zIndex (higher - top)
            } else {
                Log.d(TAG + " - change marker", "marker repositioned for dog: " + dog.getKey());
                LatLng newPosition = new LatLng(dog.getLocation().getLatitude(), dog.getLocation().getLongitude());

                markers.get(index).setPosition(newPosition);
                // TODO - last updated should always be "now" ??
                markers.get(index).setSnippet("last updated: " + HelperClass.converTimeToReadable(dog.getLocation().getTime()));
                // TODO - return opacity back to normal (for new location)
                markers.get(index).setAlpha(1.0f);

                // if info window is opened, refresh text:
                if (markers.get(index).isInfoWindowShown()) {
                    markers.get(index).showInfoWindow();
                }
            }
        } else {
            Log.w(TAG, "dog doesn't have location - name: " + dog.getName());
            // if location == null and if marker exists - remove it:
            if (markers.get(index) != null) {
                markers.get(index).remove();
            }
        }

        // for each change, recalculate and zoom the map:
        resetMapView();
    }


    /**
     * Reposition map view (bounds) and zoom level. Works only if follow is enabled - if user
     * didnt touch the map, change its zoom level or position (which automatically disables
     * any follows).
     * Recalculate what is being followed - all dogs by default, or one dog when its clicked.
     * Also checks for user location, if user has to be included in the bounds.
     *
     * Function is started whenever dog or user changes its position.
     *
     * ** If only one dog is included, or dogs are too close, zoom level is too big.
     */
    // TODO - change zoom level to see all the dogs:
    public void resetMapView() {
        // if map is touched (user changed position) - dont follow anything:
        if (!followEnabled) {
            Log.w(TAG, "resetMapView - view change disabled - map touched");
            return;
        }

        // if there are no markers, dont do anything (no included points - cant build view):
        if (isMarkersEmpty()) {
            Log.d(TAG, "resetMapView - no included points");
            return;
        }

        // TODO - (error) = if map is not finished, dont reset (if all dog listeners arent set)

        // start building bounds
        Log.d(TAG, "reset map view - follow dog(s) index: " + followDogIndex);

        // adding dog markers (all or only one)
        if (followDogIndex == -1) {
            Log.d(TAG, "map zoom on markers: \n" + markers);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            // include markers:
            for (Marker mark : markers) {
                if (mark != null) { // when is it null? (no location for dog ??)
                    builder.include(mark.getPosition());
                }
            }

            // TODO - maybe calculate padding based on the dog distance ???
            // calculate padding pixels (from display width x height):
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            // int padding = (int) (width * 0.25); // offset from edges of the map 20% of screen
            int padding = (int) (height * 0.20); // offset from edges of the map 20% of screen

            // move camera, set bounds and padding:
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
        } else {
            Log.d(TAG, "map zoom on position: \n" + markers.get(followDogIndex).getPosition());
            // set location with the zoom of 17 (otherwise its too close)
            map.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(markers.get(followDogIndex).getPosition(), MAX_ZOOM_LEVEL));
        }
    }

    private boolean isMarkersEmpty() {
        int includedPoints = 0;

        // check if there are any included points:
        for (Marker marker : markers) {
            // marker is null when there is no recent location for that dog
            if (marker != null) {
                includedPoints++;
            }
        }

        // if there are no points (marker locations), bounds cant be set
        return includedPoints == 0;
    }





























    /* get dog tracks, if the dog is clicked */


    /**
     * Shows dog tracks on the map. Shows last 5 tracks, and only if they are not far apart.
     * Time between them is set by a user.
     *
     * This method is called when the dog is clicked (user wants to follow another dog).
     * Previous listener and tracks are deleted, and new ones are set.
     *
     * @param key - key of a dog to listen
     */
    private void setDogTracksListener(final String key) {
        // TODO - basic dog for color = just in case to fix color dynamically
        Log.d(TAG, "set tracks listener - dog key: " + key);

        // remove dog listeners:
        // TODO - already done (??)
        removeDogTracksListener();
        tracksRef = FirebaseDatabase.getInstance().getReference("dogs/" + key);

        // get tracks (last 100 should suffice) and reverse the list:
        FirebaseDatabase.getInstance().getReference("dogs/" + key + "/tracks").orderByKey()
                .limitToLast(NUMBER_OF_LAST_LOCATIONS)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("TRACKS", "number of tracks: " + dataSnapshot.getChildrenCount());

                // write all to list:
                ArrayList<Tracks> tracks = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    tracks.add(snap.getValue(Tracks.class));
                }

                setDogTracks(tracks);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    // check which tracks dont overlap, and show them on the map:
    private void setDogTracks(List<Tracks> locations) {
        Log.d(TAG, "change tracks location - ???");

        // TODO - set marker color
        // get marker icon
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
        // BitmapDescriptorFactory icon22 = BitmapDescriptorFactory.fromBitmap(icon3);

        // remove existing dog tracks:
        removeDogTracks();

        // first location (big marker) already set:
        int index = locations.size() - 1;
        Tracks lastTrack = locations.get(index);

        // find next locations:
        index--;
        int count = 0;
        for (; index >= 0; index--) {
            Log.d("TAG", "tracks: " + HelperClass.converTimeToReadable(locations.get(index).getTime()));

            // check if the max allowed number of minutes passed between markers:
            if (HelperClass.calculateMinutes(lastTrack.getTime()
                    - locations.get(index).getTime()) > MAX_MIN_BETWEEN_MARKERS) {
                break;
            }

            // set marker if location is different than last, or skip:
            if (!locations.get(index).isEqual(lastTrack)) {
                // normalize opacity:
                float opacity = normalizeData(10, count);
                lastTrack = locations.get(index);

                // add marker
                tracks.add(count, map.addMarker(new MarkerOptions()
                        .position(new LatLng(lastTrack.getLatitude(), lastTrack.getLongitude()))
                        .alpha(opacity)
                        .icon(icon)
                        .draggable(false)
                ));

                // check if max number of tracks reached:
                count++;
                if (count >= MAX_NUMBER_OF_LAST_TRACKS) {
                    break;
                }
            }
        }
    }



    // normlize the data to 0-1 range
    // used for opacity calculations
    // min = 0.1 is the minimum returned
    // step = how many steps there should be (thats used to put them in range equally spaced)
    public float normalizeData(float step, float number) {
        float calc = 1 - ((step * number) / 100);

        if (calc < 0) {
            calc = (float) 0.1;
        }

        return calc;
    }


    // remove dog tracks
    // TODO - possible problems, dog tracks are checked in the listener, while user might click on something different
    private void removeDogTracks() {
        for (int i = tracks.size() - 1; i >= 0; i--) {
            // check if theyre not zero:
            tracks.get(i).remove();
            tracks.remove(i);
        }
    }

    private void removeDogTracksListener() {
        if (tracksRef != null && tracksListener != null) {
            tracksRef.removeEventListener(tracksListener);
        }
    }



































    /*
        functions for buttons = click all dogs, or only one:
     */

    // on "all dogs" button clicked - track all dogs
    private void showAllDogs() {
        Log.d(TAG, "onClick - follow all dogs");

        // remove existing dog tracks and track listeners:
        removeDogTracksListener();
        removeDogTracks();

        followEnabled = true;
        followDogIndex = -1;
        resetMapView();
    }

    // on button click - track only this dog
    private void showOnlyThisDog(Integer index) {
        // check if marker location for that dog even exists - either send message or follow dog
        if (markers.get(index) == null) {
            Toast.makeText(getContext(), "No recent location for dog: " + dogs.get(index).getName(),
                    Toast.LENGTH_SHORT).show();
            Log.w(TAG, "onClick - can't follow dog with index: " + index);
            return;
        }

        // remove previous track listeners - add new ones:
        removeDogTracksListener();
        setDogTracksListener(user.getDogs().get(index).getKey());

        Log.d(TAG, "follow dog - index: " + index);
        followEnabled = true;
        followDogIndex = index;
        resetMapView();
    }

    // map is moved, stop following
    private void stopFollowing() {
        followEnabled = false;
    }


































































    /*
        Get dogs thumbs - make list of dogs, with listeners (for follow target)
     */

    // set dogs listener:
    protected void getDogsForThumbList() {
        // TODO - make custom list adapter:
        final CustomDogList customAdapter = new CustomDogList(getActivity(), dogs);
        // listView.setAdapter(customAdapter);
        setListAdapter(customAdapter);
        // TODO - set click listener:
        // getListView().setOnItemClickListener(this);

        // TODO - firebase - get reference:
        DatabaseReference dogsRef = FirebaseDatabase.getInstance()
                .getReference("users/" + FBAuth.getUserKey() + "/dogs");
        dogsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customAdapter.clear();

                for (DataSnapshot dogSnaps : dataSnapshot.getChildren()) {
                    Dog dog = dogSnaps.getValue(Dog.class);
                    Log.d(TAG, "get all dogs - save to custom (thumb) list");
                    Log.d(TAG, dog.toString());
                    customAdapter.add(dog);
                }

                // TODO - error = nakon dohvaćanja svih pasa, postavi view ovisno o orijentaciji:
                setViewByOrientation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // set orientation, and calculate sizes:
    private void setViewByOrientation() {
        final View testView = dogThumbsLayout;
        ViewTreeObserver vto = testView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("TEST", "Height = " + testView.getHeight() + " Width = " + testView.getWidth());
                ViewTreeObserver obs = testView.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);

                // TODO - when view ready, set view depending on the orientation:
                // TODO - error - calculate size and padding (for thumbs)
                // TODO - IllegalStateException = fragment not attached to a context (after direct FB changes)
                // .. added to getActivity(). ...
                if (getActivity() != null) {
                    if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        dogThumbsLayout.setOrientation(LinearLayout.HORIZONTAL);
                        setDogThumbnailsVertical();
                    } else {
                        dogThumbsLayout.setOrientation(LinearLayout.VERTICAL);
                        // setDogThumbnailsVertical();
                        Log.w(TAG, "WRONG ORIENTATION");
                    }
                }
            }
        });
    }

    // set dog Thumbnails (calculate size) and click listeners:
    // vertical orientation
    private void setDogThumbnailsVertical() {
        // TODO - size in pixels (instead of dp):
        int size;
        int padding = 16;
        int space = 16;
        int margin = padding;

        // calculate w x h (for 5 elements in a row)
        size = (dogThumbsLayout.getWidth() - (space * dogs.size()) - (padding * 2)) / 5;
        // TODO - remove all views (if exist, for refresh) - needed ???
        dogThumbsLayout.removeViews(0, dogThumbsLayout.getChildCount());

        // add "all dogs" button:
        dogThumbsLayout.addView(addButtonAllDogs(size, padding, margin));

        // add the rest of the dogs buttons
        int numberOfDogs = dogs.size();
        for (int i = 0; i < numberOfDogs; i++) {
            dogThumbsLayout.addView(addButtonForDogs(size, padding, margin, i, numberOfDogs));
        }
    }


    /** Set "all dogs" button to see/follow all dogs at the same time.
     * @param size
     * @param padding
     * @param margin
     * @return
     */
    private ImageView addButtonAllDogs(int size, int padding, int margin) {
        ImageView buttonAll = new ImageView(getActivity());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.setMargins(0,0, margin, 0);

        buttonAll.setBackgroundColor(Color.BLACK);
        buttonAll.setImageResource(R.drawable.all_dogs_small);
        buttonAll.setScaleType(ImageView.ScaleType.FIT_CENTER);
        buttonAll.setLayoutParams(layoutParams);
        buttonAll.setPadding(padding, padding, padding, padding);

        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllDogs();
            }
        });

        return buttonAll;
    }


    /** Set image for the dog, and onclick listener to follow it.
     * @param size
     * @param padding
     * @param margin
     * @param position
     * @param numberOfDogs
     * @return
     */
    private SimpleDraweeView addButtonForDogs(int size, int padding, int margin,
                                              int position, int numberOfDogs) {
        // TODO - image with fresco:
        SimpleDraweeView imageView = new SimpleDraweeView(getContext());

        if (dogs.get(position) != null) {
            // add dog photo (if exists):
            if (dogs.get(position).getPhotoURL() ==  null) {
                imageView.setImageResource(defaultThumbs.get(position));
            } else {
                Uri uri = Uri.parse(dogs.get(position).getPhotoURL());
                imageView.setImageURI(uri);
            }

            // add background color:
            int color = HelperClass.getColorFromRes(dogs.get(position).getColor(), null,
                    getResources(), getContext());
            imageView.setBackgroundColor(color);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO - better way to get dog index
                    // get index of a clicked child
                    // index of a dog ( -1 = skip "all dogs" button)
                    int dogIndex = dogThumbsLayout.indexOfChild(v) - 1;

                    Log.d(TAG, "clicked thumb: " + dogIndex);
                    showOnlyThisDog(dogIndex);
                }
            });
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        // ako nije zadnji, staviti margin-right:
        if (position < numberOfDogs - 1) {
            layoutParams.setMargins(0,0, margin, 0);
        }
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(padding, padding, padding, padding);

        return imageView;
    }
}