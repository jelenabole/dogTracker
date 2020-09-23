package hr.tvz.trackmydog.fragments;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.activities.DogDetailsAddActivity;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.userModel.DogInfo;

public class DogsFragment extends ListFragment {

    private static final String TAG = "Dogs List Fragment";

    @BindView(R.id.noDogsLayout) LinearLayout noDogsLayout;
    @BindView(R.id.addButton) FloatingActionButton addButton;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    private DogInfoListAdapter dogInfoListAdapter;

    public static DogsFragment newInstance() {
        return new DogsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " *** on Create");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_dogs, container, false);
        ButterKnife.bind(this, v);

        // set recycler view and adapter
        dogInfoListAdapter = new DogInfoListAdapter(getActivity());
        recyclerView.setAdapter(dogInfoListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // add item decoration (divider between views):
        recyclerView.addItemDecoration(new VerticalDivider(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL, 30));

        // get CurrentUserViewModel from the ViewModelProvider utility class
        // set the observer on user (and dogs) and refresh adapter on data change:
        ViewModelProviders.of(getActivity()).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(getViewLifecycleOwner(), currentUser -> {
                    if (currentUser != null) {
                        if (currentUser.getDogs() != null) {
                            // update the UI with values from the snapshot
                            Log.d(TAG, "Current user data retrieved: " + currentUser);
                            noDogsLayout.setVisibility(View.GONE);
                            // recyclerView.scheduleLayoutAnimation();
                            dogInfoListAdapter.refreshData(currentUser.getDogs());
                        } else {
                            // remove the dogs:
                            Log.d(TAG, "Dog list is empty");
                            noDogsLayout.setVisibility(View.VISIBLE);
                            dogInfoListAdapter.refreshData(new ArrayList<DogInfo>());
                        }
                    }
                });

        // second animation (add button animation):
        /*
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int parentWidth = displaymetrics.widthPixels;
        int parentHeight = displaymetrics.heightPixels;
        */

        // add dog floating button - starts activity
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DogDetailsAddActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(TAG, " *** On View Created");
    }
}