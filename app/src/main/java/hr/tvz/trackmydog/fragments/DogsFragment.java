package hr.tvz.trackmydog.fragments;

import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.activities.DogDetailsAddActivity;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.userModel.DogInfo;
import hr.tvz.trackmydog.models.userModel.CurrentUser;

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
        // TODO - check activity as a "context":
        dogInfoListAdapter = new DogInfoListAdapter(getActivity());
        recyclerView.setAdapter(dogInfoListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // add item decoration (divider between views):
        recyclerView.addItemDecoration(new VerticalDivider(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL, 30));

        // get CurrentUserViewModel from the ViewModelProvider utility class
        // set the observer on user (and dogs) and refresh adapter on data change:
        ViewModelProviders.of(getActivity()).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(this, new Observer<CurrentUser>() {
            @Override
            public void onChanged(@Nullable CurrentUser currentUser) {
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
                        // recyclerView.scheduleLayoutAnimation();
                        dogInfoListAdapter.refreshData(new ArrayList<DogInfo>());
                    }
                }
            }
        });

        // second animation:
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int parentWidth = displaymetrics.widthPixels;
        int parentHeight = displaymetrics.heightPixels;

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