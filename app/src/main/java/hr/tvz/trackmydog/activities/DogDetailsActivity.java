package hr.tvz.trackmydog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    @BindView(R.id.backgroundBanner) LinearLayout backgroundBanner;
    @BindView(R.id.generalInfo) LinearLayout generalInfo;

    @BindView(R.id.nameText) TextView nameText;
    @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.breedText) TextView breedText;
    @BindView(R.id.ageText) TextView ageText;

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
        int dogColor = getResources().getIdentifier(dog.getColor(),
                "color", getPackageName());
        int dogColorResource = getResources().getColor(dogColor, null);
        String dogLastLocationTime = dog.getLocation() == null ? "no location detected" :
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(dog.getLocation().getTime()));

        nameText.setText(dogName);
        breedText.setText(dogBreed);
        ageText.setText(dogAge);
        dogImage.setImageResource(R.drawable.all_dogs);

        backgroundBanner.setBackgroundColor(dogColorResource);
    }

    // TODO - add all info about dogs health, and else
    // TODO - change base (loaction + info)
    // TODO - edit info enabled (add button)
    // TODO - add activity animations (image)

}
