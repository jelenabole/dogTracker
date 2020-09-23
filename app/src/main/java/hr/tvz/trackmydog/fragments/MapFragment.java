package hr.tvz.trackmydog.fragments;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.dogLocationModel.DogMarker;
import hr.tvz.trackmydog.models.dogLocationModel.Run;
import hr.tvz.trackmydog.models.dogLocationModel.ShortLocation;
import hr.tvz.trackmydog.models.dogLocationModel.Tracks;
import hr.tvz.trackmydog.models.userModel.DogInfo;
import hr.tvz.trackmydog.models.userModel.CurrentUser;

public class MapFragment extends ListFragment implements OnMapReadyCallback {

    private static final String TAG = "Map fragment";

    @BindView(R.id.dogThumbsLayout) LinearLayout dogThumbsLayout;
    @BindView(R.id.noDogsLayout) LinearLayout noDogsLayout;
    @BindView(R.id.allDogsButton) SimpleDraweeView allDogsButton;

    // recycler view:
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    private DogThumbListAdapter dogThumbListAdapter;
    private CurrentUser user;
    private MyGoogleMap myMap;

    // for tracks listeners:
    DatabaseReference tracksRef;
    ValueEventListener tracksListener;
    // number of last locations to get from FB:
    int NUMBER_OF_LAST_LOCATIONS = 20;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        myMap = new MyGoogleMap(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);

        // set adapter and recycler view (horizontal):
        dogThumbListAdapter = new DogThumbListAdapter(getActivity(), this);
        recyclerView.setAdapter(dogThumbListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));

        // add animation to the all-dogs button:
        final Animation buttonsAnimation = AnimationUtils
                .loadAnimation(getContext(), R.anim.dog_buttons_item_anim);

        // set listener to current user and get info:
        ViewModelProviders.of(getActivity()).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(getViewLifecycleOwner(), currentUser -> {
                    if (currentUser != null) {
                        user = currentUser;

                        if (currentUser.getDogs() != null) {
                            // update the UI with values from the snapshot
                            Log.d(TAG, "Current user data retrieved: " + currentUser);

                            // allDogsButton.animate();
                            allDogsButton.startAnimation(buttonsAnimation);
                            recyclerView.scheduleLayoutAnimation();
                            dogThumbListAdapter.refreshData(currentUser.getDogs());
                            showThumbs();
                        } else {
                            // remove the dogs:
                            Log.d(TAG, "Dog list is empty");
                            dogThumbListAdapter.refreshData(new ArrayList<DogInfo>());
                            hideThumbs();
                        }

                        startMap();
                    }
                });

        allDogsButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                Log.d(TAG, "all dogs clicked");
                showAllDogs();
            }
        });

        return v;
    }

    public void startMap() {
        Log.d(TAG, "map: " + myMap.getMap());
        // user is ready here
        if (myMap.getMap() == null) {
            // set map fragment:
            SupportMapFragment mapFragment;
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Checks if user has dogs, and creates their thumbnails on map.
     */
    private void showThumbs() {
        Log.w(TAG, "user has dogs");
        dogThumbsLayout.setVisibility(View.VISIBLE);
        noDogsLayout.setVisibility(View.GONE);
    }

    private void hideThumbs() {
        Log.w(TAG, "user doesn't have dogs");
        dogThumbsLayout.setVisibility(View.GONE);
        noDogsLayout.setVisibility(View.VISIBLE);
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
        Log.d(TAG, "user: " + user);
        myMap.setMap(googleMap);

        Log.d(TAG, "setting markers for dogs");
        // set dog reference for the marker, or add null (for deleted dog)
        if (user.getDogs() != null) {
            Log.d(TAG, "number of dogs: " + user.getDogs().size());
            for (int i = 0; i < user.getDogs().size(); i++) {
                if (user.getDogs().get(i) != null) {
                    setListenerOnDogLocation(user.getDogs().get(i));
                }
                myMap.addMarker(null);
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
        buttonLayoutParams.setMargins(0, 0, 30, 350);
    }


    /*  DOGS = get reference and put on the map  */

    // set dog listener (map location)
    // on change, change the marker on the map
    // recalculate the zoom level and position (same as for the user change)
    protected void setListenerOnDogLocation(final DogInfo usersDog) {
        FirebaseDatabase.getInstance().getReference("dogs/" + usersDog.getKey() + "/location")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // add important info on the dog (index, name, color):
                        DogMarker dog = new DogMarker();
                        dog.setLocation(dataSnapshot.getValue(ShortLocation.class));
                        dog.setIndex(usersDog.getIndex());
                        dog.setName(usersDog.getName());
                        dog.setColor(usersDog.getColor());
                        dog.setKey(usersDog.getKey());

                        Log.d(TAG + " dog listener", "dog from listener: \n" + dog);
                        // ... (on some FB refreshes, fragment isnt added on activity)
                        if (isAdded()) {
                            Log.d(TAG + " dog listener", "isAdded passed");
                            myMap.changeMarkerLocation(dog);
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


    /* get dog tracks, if the dog is clicked */


    /**
     * Shows dog tracks on the map. Shows last 5 tracks, and only if they are not far apart.
     * Time between them is set by a user.
     *
     * This method is called when the dog is clicked (user wants to follow another dog).
     * Previous listener and tracks are deleted, and new ones are set.
     *
     * @param dogIndex - index of a dog to listen
     */
    private void setDogTracksListener(final int dogIndex, final float startAlpha) {
        final String key = user.getDogs().get(dogIndex).getKey();
        final String color = user.getDogs().get(dogIndex).getColor();
        Log.d(TAG, "set tracks listener - dog key: " + key);

        // remove dog listeners:
        removeDogTracksListener();

        FirebaseDatabase.getInstance().getReference("dogs/" + key + "/runs").orderByKey()
                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Run> map = (Map) dataSnapshot.getValue();
                String runKey = "";
                for (Map.Entry<String, Run> entry : map.entrySet()) {
                    runKey = entry.getKey();
                }

                Log.d("TRACKS", "run ID: " + runKey);
                tracksRef = FirebaseDatabase.getInstance().getReference("dogs/" + key + "/tracks/" + runKey);

                tracksRef.orderByKey().limitToLast(NUMBER_OF_LAST_LOCATIONS)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("TRACKS", "number of tracks: " + dataSnapshot.getChildrenCount());

                            // write all to list:
                            ArrayList<Tracks> tracks = new ArrayList<>();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                tracks.add((Tracks) child.getValue(Tracks.class));
                            }

                            myMap.setDogTracks(tracks, dogIndex, color);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeDogTracksListener() {
        if (tracksRef != null && tracksListener != null) {
            tracksRef.removeEventListener(tracksListener);
        }
    }


     /* functions for buttons = click all dogs, or only one */

    // on "all dogs" button clicked - track all dogs
    private void showAllDogs() {
        Log.d(TAG, "onClick - follow all dogs");
        // remove existing dog tracks and track listeners:
        removeDogTracksListener();
        // reset zoom level:
        myMap.showAllDogs();
    }

    // on button click - track only this dog
    public void showOnlyThisDog(Integer index) {
        // check if marker location for that dog even exists - either send message or follow dog
        if (myMap.isMarkerAtIndexEmpty(index)) {
            Toast.makeText(getContext(), "No recent location for dog: "
                    + user.getDogs().get(index).getName(), Toast.LENGTH_SHORT).show();
            Log.w(TAG, "onClick - can't follow dog with index: " + index);
            return;
        }

        Log.d(TAG, "follow dog - index: " + index);

        // remove previous track listeners - add new ones:
        removeDogTracksListener();
        setDogTracksListener(index, 1f);

        myMap.showOneDog(index);
    }
}