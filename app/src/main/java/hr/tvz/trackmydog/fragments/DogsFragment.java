package hr.tvz.trackmydog.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.activities.DogAddNewActivity;
import hr.tvz.trackmydog.activities.DogDetailsActivity;
import hr.tvz.trackmydog.firebase.FBAuth;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.userModel.BasicDog;
import hr.tvz.trackmydog.utils.ResourceUtils;
import hr.tvz.trackmydog.utils.TimeUtils;

public class DogsFragment extends ListFragment {

    private static final String TAG = "Dogs List Fragment";

    // info about all dogs (get only once, or current or something ??? )
    @BindView(R.id.linearLayout) LinearLayout linearLayout;
    @BindView(R.id.addButton) FloatingActionButton addButton;

    private List<BasicDog> dogs;
    List<Integer> defaultThumbs;

    public static DogsFragment newInstance() {
        return new DogsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " *** on Create");

        // TODO - get list of dogs:
        dogs = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_dogs, container, false);
        ButterKnife.bind(this, v);

        // add dog floating button - starts activity
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DogAddNewActivity.class);
                startActivity(intent);
            }
        });

        defaultThumbs = ResourceUtils.getDefaultDogPictures();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(TAG, " *** On View Created");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, " *** On Start");

        // get dogs on start (after activity is created)
        getDogs();
    }

    /* GET DOGS - over user, not firebase listener */

    protected void getDogs() {
        List<BasicDog> basicDogs = FBAuth.getCurrentUserFB().getDogs();

        // TODO - nepotrebno (???)
        linearLayout.removeViews(0, linearLayout.getChildCount());

        // clear list of dogs - not needed (not changing on listener):
        dogs.clear();

        // if there are dogs:
        if (basicDogs != null) {
            // add all dogs to list (if theyre not null = deleted):
            for (BasicDog dog : basicDogs) {
                if (dog != null) {
                    // add each dog
                    dogs.add(dog);
                    Log.d(TAG, "add dogs to list - dog found: " + dog.getName());

                    // add it to linear layout
                    setDogFrame(dog);
                } else {
                    Log.d(TAG, "add dogs to list - dog missing");
                }
            }
        }
    }



    /* OTHER FUNCTIONS */


    // TODO - set one by one dog (linear views with pic and info):
    private void setDogFrame(BasicDog dog) {
        int padding = 16;

        // TODO - calculate boxes with width = for normal Orientation:
        /*
        System.out.println("box size: ");
        System.out.println(linearLayout.getWidth());
        System.out.println(linearLayout.getHeight());
        */

        // TODO - get and prepare dog info:
        String dogName = dog.getName() == null ? "-- unknown --" : dog.getName();
        if (dog.getAge() != null) {
            dogName += " (" + dog.getAge() + ")";
        }
        String dogBreed = dog.getBreed() == null ? "-- unknown --" : dog.getBreed();
        // TODO - context needed (sometimes fails)
        int dogColor = ResourceUtils.getDogColor(dog.getColor(), getResources(), getActivity());

        // TODO - get location currently deleted (not in BasicDog):
        // String dogLastLocationTime = HelperClass.getLastLocationTime(dog.getLocation());
        String dogLastLocationTime = TimeUtils.getLastLocationTime(null);

        // set all linear views (box, pic and info):
        LinearLayout dogFrame = new LinearLayout(getContext());
        dogFrame.setOrientation(LinearLayout.HORIZONTAL);
        dogFrame.setPadding(padding, padding, padding, padding);
        dogFrame.setWeightSum(5);
        linearLayout.addView(dogFrame);

        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        boxParams.setMargins(0, 0, 0, padding);
        dogFrame.setLayoutParams(boxParams);
        dogFrame.setBackgroundColor(Color.LTGRAY);

        // setOnClickListener for dog details:
        dogFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " *** dog clicked");

                // TODO - get index of a child (error - find better way):
                int dogIndex = dogs.get(((ViewGroup) v.getParent()).indexOfChild(v)).getIndex();
                Log.d(TAG, "show dog details - index: " + dogIndex);

                Intent dogDetailsIntent = new Intent(getActivity(), DogDetailsActivity.class);
                dogDetailsIntent.putExtra("dogIndex", dogIndex);
                startActivity(dogDetailsIntent);
            }
        });

        // TODO - set 2 linearlayouts in this:
        LinearLayout dogPicture = new LinearLayout(getContext());
        dogPicture.setOrientation(LinearLayout.VERTICAL);
        dogPicture.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams dogPictureParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT);
        dogPictureParams.weight = 1;
        dogPicture.setBackgroundColor(dogColor);
        dogPicture.setLayoutParams(dogPictureParams);

        LinearLayout dogDescription = new LinearLayout(getContext());
        dogDescription.setOrientation(LinearLayout.VERTICAL);
        dogDescription.setPadding(padding * 2, padding, padding, padding);
        dogDescription.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams dogDescriptionParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        dogDescriptionParams.weight = 4;
        dogDescription.setLayoutParams(dogDescriptionParams);

        dogFrame.addView(dogPicture);
        dogFrame.addView(dogDescription);

        // TODO - add image:
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView dogImageView = new ImageView(getContext());
        dogImageView.setImageResource(R.drawable.default_dog);
        dogImageView.setLayoutParams(imageParams);
        dogPicture.addView(dogImageView);

        // TODO - add content (description):
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView nameTextView = new TextView(getContext());
        nameTextView.setText(dogName.toUpperCase());
        nameTextView.setTextColor(dogColor);
        nameTextView.setTypeface(null, Typeface.BOLD_ITALIC);
        // TODO - set text size:
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        nameTextView.setLayoutParams(textParams);
        dogDescription.addView(nameTextView);

        TextView breedTextView = new TextView(getContext());
        breedTextView.setText(dogBreed);
        breedTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        breedTextView.setLayoutParams(textParams);
        dogDescription.addView(breedTextView);

        TextView locationTextView = new TextView(getContext());
        locationTextView.setText(dogLastLocationTime);
        locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        locationTextView.setLayoutParams(textParams);
        dogDescription.addView(locationTextView);
    }

}