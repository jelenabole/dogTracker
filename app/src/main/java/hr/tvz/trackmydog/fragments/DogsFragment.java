package hr.tvz.trackmydog.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hr.tvz.trackmydog.DogDetailsActivity;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.dogModel.CustomDogList;
import hr.tvz.trackmydog.dogModel.Dog;

public class DogsFragment extends ListFragment {

    // Firebase links:
    String dogsLink;

    // info about all dogs (get only once, or current or something ??? )
    LinearLayout linearLayout;

    private List<Dog> dogs;

    DatabaseReference dogsRef;
    List<Integer> defaultThumbs;

    public static DogsFragment newInstance() {
        return new DogsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("*************** On Create");
        dogsLink = "users/" + FBAuth.getCurrentUserFB().getKey() + "/dogs";

        // TODO - get list of dogs:
        dogs = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dogs, container, false);
        // get all dogs, and set FB reference:
        // getDogsOnce();

        defaultThumbs = HelperClass.getDefaultDogPictures();
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        System.out.println("*************** On View Created");
        // TODO - parent reset:
        linearLayout =  (LinearLayout) view.findViewById(R.id.linearLayout);
        // TODO - nepotrebno: (error)
        linearLayout.removeViews(0, linearLayout.getChildCount());
        // TODO - put listener on dogs:
        getDogs();
    }

    protected void getDogs() {
        dogsRef = FirebaseDatabase.getInstance().getReference(dogsLink);
        dogsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // TODO - delete dogs and get new list:
                dogs.clear();

                for (DataSnapshot dogSnaps : dataSnapshot.getChildren()) {
                    Dog dog = dogSnaps.getValue(Dog.class);
                    dogs.add(dog);
                    // System.out.println("DOG FOUND: " + dog.getName());
                    // setAllDogsImages();
                    // TODO - add dog to linear layout
                    setDogFrame(dog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO - set one by one dog (linear views with pic and info):
    private void setDogFrame(Dog dog) {
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
        int dogColor = HelperClass.getDogColor(dog.getColor(), getResources(), getContext());
        String dogLastLocationTime = HelperClass.getLastLocationTime(dog.getLocation());


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

        // TODO - setOnClickListener
        dogFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(" ****** kliknut pas");
                System.out.println(v);
                // TODO - get index of a child (error - find better way):

                int dogIndex = dogs.get(((ViewGroup) v.getParent()).indexOfChild(v)).getIndex();

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
        dogImageView.setImageResource(R.drawable.all_dogs);
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

    // set dog Thumbnails (calc size) and click listeners:
    private void setAllDogsImages() {
        // dohvati veličinu ekrana ???
        // TODO - calculate w x h (for 5 elements in row):
        /*
        layout.getWidth();
        int padding = 15;
        int space = 15;
        int size = (layout.getWidth() - (space * dogs.size()) - (padding * 2)) / 5;

        ConstraintSet set = new ConstraintSet();
        ImageView view = new ImageView(getContext());
        layout.addView(view,0);
        */


        /*
        view.setMinimumHeight(500);
        view.setMinimumWidth(500);
        */

        /*
        view.setBackgroundColor(Color.RED);
        set.clone(layout);
        view.setId(View.generateViewId());
        set.connect(view.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 60);
        set.applyTo(layout);
        */

        /*
        // postaviti linearni layout za svakog psa
        // TODO - remove child first ???
        linearLayout.removeViews(0, linearLayout.getChildCount());
        linearLayout.setMinimumHeight(300);

        LinearLayout dogBox = new LinearLayout(getContext());
        dogBox.setOrientation(LinearLayout.VERTICAL);
        dogBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 200));

        dogBox.setBackgroundColor(Color.RED);
        */
        /*
        dogBox.getLayoutParams().height = 200;
        dogBox.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        */



        /*
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView titleView = new TextView(getContext());
        titleView.setLayoutParams(lparams);
        // titleView.setTextAppearance(getContext(), android.R.attr.textAppearanceLarge);
        titleView.setText("Hellooooo!");

        // getContext().setContentView(layout);
        linearLayout.addView(titleView);
        */


        /*
        // TODO - Button for "all" dogs:
        // TODO - nekakva neutralna boja (default "all" gumb)

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
                int color = res.getColor(
                        res.getIdentifier(dogs.get(position).getColor(), "color",
                                getContext().getPackageName()),
                        getContext().getTheme());
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
                                         + " - not successful - NO INFO", Toast.LENGTH_SHORT).show();
                             }
                         }
                     }
                     }
                );
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
            */
    }


    protected void getDogsOnce() {
        // TODO - make custom list adapter:
        final CustomDogList customAdapter = new CustomDogList(getActivity(), dogs);
        // listView.setAdapter(customAdapter);
        setListAdapter(customAdapter);

        // TODO - set click listener:
        // getListView().setOnItemClickListener(this);

        // TODO - firebase - get reference:
        dogsRef = FirebaseDatabase.getInstance().getReference(dogsLink);
        dogsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customAdapter.clear();

                for (DataSnapshot dogSnaps : dataSnapshot.getChildren()) {
                    Dog dog = dogSnaps.getValue(Dog.class);
                    System.out.println("DOG *******************");
                    System.out.println(dog);

                    customAdapter.add(dog);
                    // TODO - set thumbnails of a dogs
                    // setAllDogsImages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



}