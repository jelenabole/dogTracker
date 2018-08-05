package hr.tvz.trackmydog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import hr.tvz.trackmydog.dogModel.Dog;
import hr.tvz.trackmydog.localDB.DbFlowApp;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class DogDetailsActivity extends AppCompatActivity {

    // TODO - info from DbFlowApp:
    private String dogLink;
    private DatabaseReference dogRef;
    private CurrentUser user;
    private Dog dog;

    LinearLayout backgroundBanner;
    LinearLayout generalInfo;

    TextView nameText;
    ImageView dogImage;
    TextView breedText;
    TextView ageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("DogDetails - On Create");

        // TODO - need to initialize Fresco before the contectView and using it:
        setContentView(R.layout.activity_dog_details);

        backgroundBanner = (LinearLayout) findViewById(R.id.backgroundBanner);
        generalInfo = (LinearLayout) findViewById(R.id.generalInfo);

        nameText = (TextView) findViewById(R.id.nameText);
        dogImage = (ImageView) findViewById(R.id.dogImage);
        breedText = (TextView) findViewById(R.id.breedText);
        ageText = (TextView) findViewById(R.id.ageText);

        // TODO - get user and get dog index = get their info:
        user = ((DbFlowApp) getApplication()).getFirebaseUser();
        int dogIndex = getIntent().getIntExtra("dogIndex", -1);
        dogLink = "users/" + user.getKey() + "/dogs/" + dogIndex;

        getDogDetails();
    };

    protected void getDogDetails() {
        dogRef = FirebaseDatabase.getInstance().getReference(dogLink);
        dogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO - get dog info:
                dog = dataSnapshot.getValue(Dog.class);
                System.out.println(dataSnapshot);
                setDogInfo(dog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDogInfo(Dog dog) {
        System.out.println("Get dog info: " + dog.getIndex());

        // TODO - get and prepare dog info:
        String dogName = dog.getName() == null ? "-- unknown --" : dog.getName();
        String dogAge = dog.getAge() == null ? "-- unknown --" : dog.getAge().toString();
        String dogBreed = dog.getBreed() == null ? "-- unknown --" : dog.getBreed();
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
