package hr.tvz.trackmydog.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.dogModel.CustomDogList;
import hr.tvz.trackmydog.dogModel.Dog;
import hr.tvz.trackmydog.localDB.DbFlowApp;
import hr.tvz.trackmydog.localDB.User;
import hr.tvz.trackmydog.userModel.CurrentUser;

// TODO:
// - camera max zoom level (when there is one dog, or close dogs, zoom is too big)
// .. but let the user zoom more (so setMaxZoom is not the option)
// - map layout = preko cijelog ekrana, a linearni iznad njega
// - linearni layout = samo jedan gumb, a na klik tog da se prošire i ostali
public class MapFragment extends ListFragment implements OnMapReadyCallback {

    boolean followEnabled = true; // if button is pressed, map not touched
    int followDogIndex = -1; // follow all dogs
    float maxZoomLevel = 17; // max map zoom level (if dogs are too close, or only one)
    // Zoom level:
    // 1 - world, 5 - continent
    // 10 - city, 15 streets, 20 - buildings (21 - EU,USA, 22-23 max)

    // TODO  - linear layout = dinamično postavljanje slika
    @BindView(R.id.linearLayout) LinearLayout linearLayout;
    @BindView(R.id.buttonAll) ImageView buttonAll;

    List<Integer> defaultThumbs;

    // TODO - marker for the animal:
    List<Marker> markers;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    // TODO - list view for quizzes:
    private List<Dog> dogs;
    private GoogleMap map;
    private SupportMapFragment mapFragment;

    User localUser;
    CurrentUser user;

    @Override
    // TODO - called on refresh too (changing screen orientation):
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("*************** On Create");

        // TODO (error) - changed function (current to My)
        localUser = ((DbFlowApp) getActivity().getApplication()).getMyUser();
        System.out.println(localUser);

        // TODO - user == null (error):
        user = ((DbFlowApp) getActivity().getApplication()).getFirebaseUser();
        System.out.println(user);
        dogs = new ArrayList<>();
        defaultThumbs = HelperClass.getDefaultDogPictures();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        System.out.println("*************** Create View");

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        // TODO - butterknife bind
        ButterKnife.bind(this, v);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO - get dogs:
        getDogsForThumbList();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("*************** Activity created");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        System.out.println("*************** On View Created");
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
        System.out.println("*************** On Map Ready");

        map = googleMap;
        // TODO - remove tilt:
        map.getUiSettings().setRotateGesturesEnabled(false);
        // TODO - set max zoom level:
        map.setMaxZoomPreference(maxZoomLevel);

        // TODO - if map is clicked (zoomed or moved) - stop following:
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                // TODO - error - only first gesture needed:
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    // double-tap (zoom) or drag
                    System.out.println("MAP TOUCHED - DISABLE FOLLOW");
                    followEnabled = false;
                    // TODO - error - let the user zoom in as it wants:
                    map.setMaxZoomPreference(25);
                    // map.resetMinMaxZoomPreference();

                    Toast.makeText(getActivity(), "The user gestured on the map.",
                            Toast.LENGTH_SHORT).show();
                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_API_ANIMATION) {
                    // tapped marker on the map or double-tap anywhere
                    Toast.makeText(getActivity(), "The user tapped something on the map.",
                            Toast.LENGTH_SHORT).show();
                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_DEVELOPER_ANIMATION) {
                    // app changes:
                    Toast.makeText(getActivity(), "The app moved the camera.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO - check if user has permission to access the location:
        // TODO - Check whether GPS tracking is enabled - to show current user location:
        // Check whether this app has access to the location permission

        // If the location permission has been granted, then start the TrackerService
        // TODO - returns -1 when theres no permission:
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != -1) {
            map.setMyLocationEnabled(true);
        }

        // TODO - provjeriti da li postoje svi psi:
        // za sve pse dohvatiti reference:
        System.out.println("TRACKING STARTED");
        System.out.println(user.getDogs());
        System.out.println(user.getDogs().size());

        // TODO - get all dogs, and set markers:
        // TODO - error - dohvatiti sve reference posebno (radi promjene pojedinog psa)
        // set dog reference for the marker, or add null (for deleted dog)
        markers = new ArrayList<>();
        for (int i = 0; i < user.getDogs().size(); i++) {
            // TODO - null / dog = if dog check
            if (user.getDogs().get(i) != null) {
                // set the index on the dog:
                // user.getDogs().get(i).setIndex(i);
                setListenerOnDogLocation(i);
            }
            // TODO - just add null, and change it with marker later:
            markers.add(null);
        }

        // TODO - create on change method for user:
        // TODO - error - on each user location change, update map

        /*
        View mapView = mapFragment.getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);rlp.setMargins(0,0,30,30);
        */
    }





    /*
        DOGS = get reference and put on the map
    */

    // set dog listener (map location)
    // on change, change the marker on the map
    // recalculate the zoom level and position (same as for the user change)
    protected void setListenerOnDogLocation(int dogIndex) {
        // TODO - firebase - get reference:
        DatabaseReference dogRef = FirebaseDatabase.getInstance()
                .getReference("users/" + user.getKey() + "/dogs/" + dogIndex);
        dogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // TODO - on firebase DB refreshes, fragment isnt added on activity:
                // TODO - isAdded() needed ??? error
                if (isAdded()) {
                    changeMarkerLocation(dataSnapshot.getValue(Dog.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // change the location of the dog marker on the map
    // set dog locationa and name/time info on marker
    private void changeMarkerLocation(Dog dog) {
        System.out.println("GET DOG LOCATION ****");
        int index = dog.getIndex();
        // TODO - get marker for the dog, change the location:
        int icon = HelperClass.getPawMarker(dog.getColor(), getResources(), getContext());

        // TODO - add position and the marker:
        if (dog.getLocation() != null) {
            LatLng position = new LatLng(dog.getLocation().getLatitude(), dog.getLocation().getLongitude());
            if (markers.get(index) != null) {
                markers.get(index).remove();
            }
            markers.set(index, map.addMarker(new MarkerOptions()
                    .position(position)
                    .alpha(1.0f)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.paw))
                    .icon(BitmapDescriptorFactory.fromResource(icon))
                    .title(dog.getName())
                    .snippet("last updated: " + HelperClass.converTimeToReadable(dog.getLocation().getTime()))
            )); // TODO - add zIndex (higher - top)

            // TODO - for each change, recalculate and zoom the map:
            resetMapView();
        }
    }


    // TODO - change zoom level to see all the dogs:
    // recalculate the view if needed (if map is not touched)
    // add the marker for the user (current location)
    // TODO - error = prikazati sve na početku, pokrenuti funkciju kad se prati lokacija
    // .. ili kod promjene mjesta psa (kojeg se prati) ili usera (ako ga se prati)
    public void resetMapView() {
        System.out.println(" \n ******** change the map view");

        // TODO - if map is touched - dont follow anything:
        if (!followEnabled) {
            return;
        }

        // TODO - else follow dog(s) - start building views (markers):
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // TODO - start adding markers (for all dogs or only one):
        if (followDogIndex == -1) {
            for (Marker mark : markers) {
                if (mark != null) { // when is it null? (no location for dog ??)
                    builder.include(mark.getPosition());
                }
            }
        } else {
            // TODO - error - check if the mark is null, needed ???
            builder.include(markers.get(followDogIndex).getPosition());
        }

        // TODO - error - get user location and pin in on map:
        // .. simpler way ???
        if (user.isFollow()) {
            // builder.include() = user location
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager;
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                builder.include(userLocation);
            }
        }

        // TODO - build view, and set padding for map markers:
        // build bounds on it:
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        // int padding = (int) (width * 0.25); // offset from edges of the map 20% of screen
        int padding = (int) (height * 0.20); // offset from edges of the map 20% of screen

        map.animateCamera(CameraUpdateFactory
                .newLatLngBounds(bounds, width, height, padding));

        // TODO - error - check zoom level, and correct it:
        System.out.println("ZOOM level: " + map.getCameraPosition());
    }





    /*
        functions for buttons = click all dogs, or only one:
     */

    // TODO - onClick for the "all dogs" button (track all dogs):
    private void showAllDogs() {
        Toast.makeText(getContext(), "SHOW ALL - not implemented", Toast.LENGTH_SHORT).show();
        followDogIndex = -1;
        followEnabled = true;
        resetMapView();
    }

    // TODO - show only one dog on click:
    private void showOnlyThisDog(Dog dog) {
        System.out.println("ZOOM IN on that dog: " + dog.getName());
        followDogIndex = dog.getIndex();
        followEnabled = true;
        resetMapView();
    }


    // TODO - if map is touched = remove following:
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
                .getReference("users/" + user.getKey() + "/dogs");
        dogsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customAdapter.clear();

                for (DataSnapshot dogSnaps : dataSnapshot.getChildren()) {
                    Dog dog = dogSnaps.getValue(Dog.class);
                    System.out.println("DOG *******************");
                    System.out.println(dog);

                    customAdapter.add(dog);
                }

                // TODO - set thumbnails of a dogs
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
        // TODO - error - dodati računanje veličine i paddinga
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            setDogThumbnailsVertical();
        } else {
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            // setDogThumbnailsVertical();
            System.out.println("WRONG ORIENTATION");
        }
    }

    // set dog Thumbnails (calc size) and click listeners:
    // vertical orientation
    // set listeners to buttons (follow all or one dog)
    private void setDogThumbnailsVertical() {
        // TODO - size in pixels (instead of dp):
        int size;
        int padding = 16;
        int space = 16;

        // TODO - calculate w x h (for 5 elements in row):
        linearLayout.getWidth();
        size = (linearLayout.getWidth() - (space * dogs.size()) - (padding * 2)) / 5;

        // TODO - Button for "all" dogs:
        // TODO - nekakva neutralna boja (default "all" gumb)
        buttonAll.setBackgroundColor(Color.BLACK);
        buttonAll.getLayoutParams().height = size;
        buttonAll.getLayoutParams().width = size;
        buttonAll.setPadding(padding, padding, padding, padding);

        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllDogs();
            }
        });

        // TODO - remove all images (except the All button):
        linearLayout.removeViews(1, linearLayout.getChildCount() - 1);

        int number = dogs.size();
        for (int i = 0; i < number; i++) {
            // TODO - image with fresco:
            int position = i;
            SimpleDraweeView imageView = new SimpleDraweeView(getContext());

            if (dogs.get(position) != null) {
                if (dogs.get(position).getPhotoURL() ==  null) {
                    imageView.setImageResource(defaultThumbs.get(position));
                } else {
                    Uri uri = Uri.parse(dogs.get(position).getPhotoURL());
                    imageView.setImageURI(uri);
                }

                // TODO - check if dog has color (or add random):
                int color = HelperClass.getColorFromRes(dogs.get(position).getColor(),
                        getResources(), getContext());
                imageView.setBackgroundColor(color);

                imageView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         // get parent, get index of a clicked child:
                         // -1 = skip the first "all" button
                         Dog dog = dogs.get(linearLayout.indexOfChild(v) - 1);

                         if (dog != null) {
                             if (dog.getLocation() != null) {
                                 showOnlyThisDog(dog);
                                 Toast.makeText(getContext(), "TRACK: " + dog.getName(),
                                         Toast.LENGTH_SHORT).show();
                             } else {
                                 Toast.makeText(getContext(), "TRACK " + dog.getName()
                                         + " - failed - NO INFO", Toast.LENGTH_SHORT).show();
                             }
                         }
                     }
                });
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size,size);
            // ako nije zadnji, staviti padding-right:
            if (i < number - 1) {
                layoutParams.setMargins(0,0, space, 0);
            }
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(padding, padding, padding, padding);

            linearLayout.addView(imageView);
        }
    }

}