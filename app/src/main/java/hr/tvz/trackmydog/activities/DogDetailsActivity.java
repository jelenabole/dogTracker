package hr.tvz.trackmydog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.firebaseServices.FBAuth;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.dogModel.Dog;
import hr.tvz.trackmydog.utils.LabelUtils;
import hr.tvz.trackmydog.utils.ResourceUtils;

public class DogDetailsActivity extends BaseActivity {

    private static final String TAG = "Dog Details Activity";

    private String dogLink;
    private Dog dog;
    private Integer dogIndex;

    @BindView(R.id.infoBanner) LinearLayout infoBanner;
    @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.sep1) View sep1;
    @BindView(R.id.sep2) View sep2;
    @BindView(R.id.nameText) TextView nameText;
    @BindView(R.id.breedText) TextView breedText;
    @BindView(R.id.ageText) TextView ageText;

    // basic info:
    @BindView(R.id.weightText) TextView weightText;
    @BindView(R.id.heightText) TextView heightText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create");

        setContentView(R.layout.activity_dog_details);
        ButterKnife.bind(this);
        hideProgressDialog();

        // TODO - get user and get dog index = get their info:
        dogIndex = getIntent().getIntExtra("dogIndex", -1);
        // means the activity has been paused:
        if (dogIndex != -1) {
            Log.d(TAG, "show details of dog with index: " + dogIndex);
            dogLink = "users/" + MyApplication.getUserKey() + "/dogs/" + dogIndex;
            getDogDetails();
        }
    };

    /**
     * Get current dog info (listener).
     */
    protected void getDogDetails() {
        FirebaseDatabase.getInstance().getReference(dogLink)
            .addListenerForSingleValueEvent(new ValueEventListener() {
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
        String dogName = LabelUtils.getAsStringLabel(dog.getName());
        String dogAge = LabelUtils.getAsStringLabel(dog.getAge()) + " yr";
        String dogBreed = LabelUtils.getAsStringLabel(dog.getBreed());
        String dogLastLocationTime = dog.getLocation() == null ? "no location detected" :
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(dog.getLocation().getTime()));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(dogName.toUpperCase());
        }
        nameText.setText(dogName);
        breedText.setText(dogBreed);
        ageText.setText(dogAge);

        // basic info:
        weightText.setText(LabelUtils.getAsStringLabel(dog.getWeight()));
        heightText.setText(LabelUtils.getAsStringLabel(dog.getHeight()));

        changeColorTo(dog.getColor());

        // TODO - image with fresco:
        /*
        if (dog.getPhotoURL() ==  null) {
            dogImage.setImageResource(R.drawable.default_dog_long);
        } else {
            Uri uri = Uri.parse(dog.getPhotoURL());
            dogImage.setImageURI(uri);
        }
        */
    }

    private void changeColorTo(String colorName) {
        int color = ResourceUtils.getColorFromRes(colorName, null,
                getResources(), this);
        int colorOpaque = ResourceUtils.getColorFromRes(colorName, "_op",
                getResources(), this);
        int colorText = ResourceUtils.getColorFromRes(colorName,"_text",
                getResources(), this);

        // BANNER - background and separators:
        infoBanner.setBackgroundColor(colorOpaque);
        sep1.setBackgroundColor(colorText);
        sep2.setBackgroundColor(colorText);

        // BANNER - text color:
        nameText.setTextColor(colorText);
        ageText.setTextColor(colorText);
        breedText.setTextColor(colorText);

        // label = dogColor
    }

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
            Log.d(TAG, "edit dog info: " + dog.getIndex());
            Intent intent = new Intent(this, DogDetailsEditActivity.class);
            intent.putExtra("dogIndex", dog.getIndex());
            startActivityForResult(intent, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "returned from editing dog info");
        if (resultCode == RESULT_OK && requestCode == 0) {
            Toast.makeText(this, "Dog info Saved", Toast.LENGTH_SHORT).show();
        }
    }

}
