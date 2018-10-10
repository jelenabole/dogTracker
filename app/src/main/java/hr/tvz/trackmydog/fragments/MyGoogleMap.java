package hr.tvz.trackmydog.fragments;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.dogLocationModel.DogMarker;
import hr.tvz.trackmydog.models.dogLocationModel.Tracks;
import hr.tvz.trackmydog.utils.ResourceUtils;
import hr.tvz.trackmydog.utils.TimeUtils;

public class MyGoogleMap {

    private static final String TAG = "Google Map";

    private GoogleMap map;
    private Context context;

    private List<Marker> tracks;
    private List<Marker> markers; // animal markers

    private boolean followEnabled = true; // if button is pressed, map not touched
    private int followDogIndex = -1; // follow all dogs
    private final float MAX_ZOOM_LEVEL = 19; // max map zoom level (if dogs are too close, or only one)
    private boolean setZoom = true;

    // for setting up markers:
    private final int MAX_HOURS_PASSED_FOR_MARKER = 24; // longer not showing
    private final int MAX_MIN_BETWEEN_MARKERS = 10; // longer not showing
    private final int MAX_NUMBER_OF_LAST_TRACKS = 6; // the ones that are not the same, and not > 10 mins apart
    private final float MIN_OPACITY = 0.1f; // important for normalizing time that passed

    MyGoogleMap(Context context) {
        this.context = context;

        markers = new ArrayList<>();
        tracks = new ArrayList<>();
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
        setMapSettings();
    }

    // set map settings:
    private void setMapSettings() {
        // set map style:
        try {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_json));
        } catch (Exception ex) {
            // possible exceptions = Resources.NotFoundException, no Context, parsing error, ...
            Log.w(TAG, "stlye not added - error: " + ex.getMessage());
        }

        // remove tilt, map toolbar and zoom buttons:
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);

        // map listeners - if map is clicked (zoomed or moved) - stop following:
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                // on user gestures or buttons clicked = hide info windows
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    // double-tap (zoom) or drag
                    // changes user made = move, double-tap (zoom)
                    Log.w(TAG, "MAP TOUCHED - DISABLE FOLLOW");
                    stopFollowing();

                    // hide info windows on change
                    hideInfoWindows();
                } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
                    hideInfoWindows();
                }
            }
        });

        // set on click listener = go to my location and stop following dogs:
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                stopFollowing();

                // false = call the super method (default behavior):
                return false;
            }
        });

        // set on marker click = only show the info window, dont zoom or change camera view
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // other markers windows are hidden automatically
                Log.d(TAG, "marker clicked - show info window");
                marker.showInfoWindow();

                // false = launches default behaviour, true = does nothing
                return true;
            }
        });

        // click on a info window to hide it
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        // returns -1 when there's no permission
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != -1) {
            Log.d(TAG, "location permission enabled - set user location");
            map.setMyLocationEnabled(true);
        }
    }

    // when camera is moved by the user, or user clicked on some button - move all info windows
    private void hideInfoWindows() {
        for (Marker mark : markers) {
            if (mark != null && mark.isInfoWindowShown()) {
                mark.hideInfoWindow();
            }
        }
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
    void changeMarkerLocation(DogMarker dog) {
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
                long diff = TimeUtils.differenceBetweenCurrentTimeInHours(dog.getLocation().getTime());
                Log.d(TAG, "last location before (h): " + diff);
                if (diff > MAX_HOURS_PASSED_FOR_MARKER) {
                    // if longer than 2 hours, dont add marker
                    // TODO - check if anything else is needed
                    return;
                }

                Log.d(TAG + " - change marker", "(new) marker positioned for dog: " + dog.getName());
                LatLng newPosition = new LatLng(dog.getLocation().getLatitude(), dog.getLocation().getLongitude());

                // calculate opacity by passed time (norm to 0-1 range):
                // more time = lower opacity (1 - ...)
                float opacity = normalizeData(diff);

                Log.e(TAG, "dog - last location time: " + dog.getLocation().getTime());
                Log.d(TAG, "opacity: " + opacity);

                int icon = ResourceUtils.getPawMarker(dog.getColor(), context.getResources(), context);
                markers.set(index, map.addMarker(new MarkerOptions()
                        .position(newPosition)
                        .icon(BitmapDescriptorFactory.fromResource(icon))
                        .alpha(opacity)
                        .title(dog.getName())
                        .snippet("last updated: " + TimeUtils.converTimeToReadable(dog.getLocation().getTime()))
                ));
                // TODO - add zIndex (higher - top)
            } else {
                Log.d(TAG + " - change marker", "marker repositioned for dog: " + dog.getKey());
                LatLng newPosition = new LatLng(dog.getLocation().getLatitude(), dog.getLocation().getLongitude());

                markers.get(index).setPosition(newPosition);
                // currently updated - time "now" and full opacity:
                markers.get(index).setSnippet("last updated: now");
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
    void resetMapView() {
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
            int width = context.getResources().getDisplayMetrics().widthPixels;
            int height = context.getResources().getDisplayMetrics().heightPixels;
            // int padding = (int) (width * 0.25); // offset from edges of the map 20% of screen
            int padding = (int) (height * 0.20); // offset from edges of the map 20% of screen

            // move camera, set bounds and padding:
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
        } else {
            Log.d(TAG, "map zoom on position: \n" + markers.get(followDogIndex).getPosition());
            // set location with the zoom of 17 (otherwise its too close)
            if (setZoom) {
                map.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(markers.get(followDogIndex).getPosition(), MAX_ZOOM_LEVEL));
                setZoom = false;
            } else {
                map.animateCamera(CameraUpdateFactory
                        .newLatLng(markers.get(followDogIndex).getPosition()));
            }

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



    /* SET dog tracks */

    /**
     * Finds last few dog's locations and puts dot markers on them.
     * Only markers with different locations and not far apart (by time) are shown.
     * Their opacity starts below main marker's opacity, and then it reduces until min value.
     *
     * @param locations - list of dog previous locations
     * @param color - dog color
     * @param startAlpha - starting opacity (alpha of the dog's main marker)
     */
    // TODO - start alpha isnt set properly
    void setDogTracks(List<Tracks> locations, String color, float startAlpha) {
        Log.d(TAG, "change tracks location - color: " + color);

        // get track-marker icon
        BitmapDescriptor icon = BitmapDescriptorFactory
                .fromResource(ResourceUtils.getDotMarker(color, context.getResources(), context));
        // grey icon:
        // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot_grey);

        // remove existing dog tracks:
        removeDogTracks();

        // first location (big marker) already set:
        int index = locations.size() - 1;
        Tracks lastTrack = locations.get(index);

        // find next locations:
        index--;
        int count = 0;
        for (; index >= 0; index--) {
            Log.d("TAG", "tracks: " + TimeUtils.converTimeToReadable(locations.get(index).getTime()));

            // check if the max allowed number of minutes passed between markers:
            if (TimeUtils.convertToMinutes(lastTrack.getTime()
                    - locations.get(index).getTime()) > MAX_MIN_BETWEEN_MARKERS) {
                break;
            }

            // set marker if location is different than last, or skip:
            if (!locations.get(index).isEqual(lastTrack)) {
                // normalize opacity:
                float opacity = normalizeDataToRange(MAX_NUMBER_OF_LAST_TRACKS, count, startAlpha);
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


    /**
     * Returns normalized number to range 0.3 to 0.8.
     * Used to calculate opacity of the additional track markers.
     * Returned value is reversed, since lower indices should have higher opacity.
     *
     * @param maxValue - number of max indices (to equally space the values)
     * @param index - index of the current calculation (used with +1)
     * @param maxRange - max possible value of the range (depends of opacity of main marker)
     * @return - normalized number to 0-1 range
     */
    // TODO - correct function:
    private float normalizeDataToRange(float maxValue, float index, float maxRange) {
        // turn the values around (1 - ...)
        float max = 1 - MIN_OPACITY - 0.1f;
        float min = 1 - maxRange + 0.1f;

        // if max range is lower then min opacity, just return it
        if (max <= min + 0.05) {
            return MIN_OPACITY;
        }

        // starting from 1 (index too):
        float minValue = 1;
        float value = index + 1;

        float rangeDiff = (max - min) / (maxValue - minValue);
        float norm = min + (rangeDiff * (value - minValue));

        // return the reversed value (biggest number should be min opacity):
        return 1f - norm;
    }

    /**
     * Returns normalized number to range 0-1 (min returned is 0.1).
     * Used to calculate opacity of the main markers.
     * Returned value is reversed, since bigger number should have opacity closer to 0.
     *
     * @param value - given number between min and max that needs to be normalized
     * @return - normalized number (from min to max) to range 0 - 1
     */
    private float normalizeData(float value) {
        // max possible number, min possible number and current value:
        float norm = ((value - MIN_OPACITY) / (MAX_HOURS_PASSED_FOR_MARKER - MIN_OPACITY));

        // return the reversed value (biggest number should be min opacity):
        return 1f - norm;
    }

    // remove dog tracks
    // TODO - possible problems, dog tracks are checked in the listener, while user might click on something different
    void removeDogTracks() {
        for (int i = tracks.size() - 1; i >= 0; i--) {
            // check if theyre not zero:
            tracks.get(i).remove();
            tracks.remove(i);
        }
    }

    // map is moved, stop following
    private void stopFollowing() {
        followEnabled = false;
    }


    /* NEW functions */

    boolean isMarkerAtIndexEmpty(int index) {
        return markers.get(index) == null;
    }

    void showAllDogs() {
        setZoom = true;
        followEnabled = true;
        followDogIndex = -1;
    }

    void showOneDog(int index) {
        // reset zoom level:
        setZoom = true;
        followEnabled = true;
        followDogIndex = index;
    }

    void addMarker(Marker marker) {
        markers.add(marker);
    }
}