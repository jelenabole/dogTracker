package hr.tvz.trackmydog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.dogModel.Dog;

public class DogDetailsActivity extends AppCompatActivity {

    private static final String TAG = "Dog Details Activity";

    // TODO - info from DbFlowApp:
    private String dogLink;
    private DatabaseReference dogRef;
    private Dog dog;

    @BindView(R.id.generalInfo) LinearLayout generalInfo;

    @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.breedText) TextView breedText;
    @BindView(R.id.ageText) TextView ageText;

    @BindView(R.id.infoLayout) LinearLayout infoLayout;
    @BindView(R.id.sep1) View sep1;
    @BindView(R.id.sep2) View sep2;
    @BindView(R.id.weightText) TextView weightText;
    @BindView(R.id.milesText) TextView milesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create");

        setContentView(R.layout.activity_dog_details);
        ButterKnife.bind(this);

        // TODO - get user and get dog index = get their info:
        int dogIndex = getIntent().getIntExtra("dogIndex", -1);
        dogLink = "users/" + FBAuth.getUserKey() + "/dogs/" + dogIndex;

        getDogDetails();
    };

    /**
     * Get current dog info (listener).
     */
    protected void getDogDetails() {
        dogRef = FirebaseDatabase.getInstance().getReference(dogLink);
        dogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dog = dataSnapshot.getValue(Dog.class);
                setDogInfo(dog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDogInfo(Dog dog) {
        Log.d(TAG, "Get dog info: " + dog.getIndex());

        // TODO - get and prepare dog info:
        String dogName = HelperClass.getAsStringLabel(dog.getName());
        String dogAge = HelperClass.getAsStringLabel(dog.getAge());
        String dogBreed = HelperClass.getAsStringLabel(dog.getBreed());
        String dogLastLocationTime = dog.getLocation() == null ? "no location detected" :
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(dog.getLocation().getTime()));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(dogName);
        }
        breedText.setText(dogBreed);
        ageText.setText(dogAge);

        changeColorTo(dog.getColor());

        // TODO - image with fresco:
        /*
        if (dog.getPhotoURL() ==  null) {
            dogImage.setImageResource(R.drawable.default_dog);
        } else {
            Uri uri = Uri.parse(dog.getPhotoURL());
            dogImage.setImageURI(uri);
        }
        */
    }

    private void changeColorTo(String colorName) {
        int color = HelperClass.getColorFromRes(colorName,
                getResources(), this);
        int colorOpaque = HelperClass.getColorFromRes(colorName + "_op",
                getResources(), this);
        int colorText = HelperClass.getColorFromRes(colorName + "_text",
                getResources(), this);

        // info layout
        infoLayout.setBackgroundColor(colorOpaque);
        breedText.setBackgroundColor(colorOpaque);
        sep1.setBackgroundColor(colorText);
        sep2.setBackgroundColor(colorText);

        // white text:
        ageText.setTextColor(colorText);
        weightText.setTextColor(colorText);
        milesText.setTextColor(colorText);
        breedText.setTextColor(colorText);

        // label = dogColor
    }

    // TODO - add all info about dogs health, and else
    // TODO - change base (loaction + info)
    // TODO - edit info enabled (add button)
    // TODO - add activity animations (image)

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit) {
            Log.d(TAG, "edit dog info");
            // do something here
        }
        return super.onOptionsItemSelected(item);
    }


}