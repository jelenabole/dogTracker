package hr.tvz.trackmydog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.forms.DogForm;

public class DogAddNewActivity extends AppCompatActivity {

    private static final String TAG = "Add New Dog Activity";

    private Integer dogIndex;
    private String dogKey;
    private String userToken;

    @BindView(R.id.error) TextView error;
    @BindView(R.id.saveButton) Button saveButton;

    // @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.chipNumber) TextView chipNumber;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.breed) TextView breed;
    @BindView(R.id.age) TextView age;
    @BindView(R.id.height) TextView height;
    @BindView(R.id.weight) TextView weight;
    @BindView(R.id.female) RadioButton female;
    @BindView(R.id.male) RadioButton male;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on Create");

        setContentView(R.layout.activity_dog_add);
        ButterKnife.bind(this);

        // TODO - get user notification token:
        userToken = FBAuth.getCurrentUserFB().getToken();

        // TODO - get new dog index - number of dogs:
        if (FBAuth.getCurrentUserFB().getDogs() != null) {
            dogIndex = FBAuth.getCurrentUserFB().getDogs().size();
        } else {
            dogIndex = 0;
        }
        Log.d(TAG, "Dog index: " + dogIndex);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDog();
            }
        });
    };

    // on button clicked
    private void saveDog() {
        error.setVisibility(View.GONE);

        // check if mandatory fields are entered:
        if (chipNumber.getText().toString().length() < 2 || name.getText().length() < 1) {
            Log.w(TAG, "error - empty fields: ("
                    + chipNumber.getText() + ") - (" + name.getText() + ")");
            error.setText("Some fields are empty!");
            error.setVisibility(View.VISIBLE);
            return;
        }

        final String dogCode = chipNumber.getText().toString();
        Log.d(TAG, "get dog from firebase - by code: " + dogCode);
        final DatabaseReference dogsRef = FirebaseDatabase.getInstance().getReference("dogs");

        // check if chip is already in use:
        dogsRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               boolean found = false;
               for (DataSnapshot dogSnaps : dataSnapshot.getChildren()) {
                   if (dogSnaps.child("code").getValue().equals(dogCode)) {
                       // if dog is found:
                       found = true;
                       dogKey = dogSnaps.getKey();

                       // check if chip is already in use:
                       if (dogSnaps.child("token").getValue() != null) {
                           Log.d(TAG, "chip already added to user");
                           error.setText("Chip already added to user!");
                           error.setVisibility(View.VISIBLE);

                           break;
                       }

                       saveDogInfo();
                       break;
                   }
               }

               // if dog is not found, send error message
               if (!found) {
                   Log.d(TAG, "dog not found FB");
                   error.setText("Wrong chip number!");
                   error.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               // Toast.makeText(this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
               error.setText("Error while saving!");
               error.setVisibility(View.VISIBLE);
           }
        });
    }

    // save dog and finish activity
    private void saveDogInfo() {
        Log.d(TAG, "save dog info - index: " + dogIndex);
        DogForm dog = new DogForm();

        // TODO - add dog index (needed ??):
        dog.setIndex(dogIndex);
        dog.setKey(dogKey);
        dog.setName(name.getText().toString());
        dog.setBreed(HelperClass.getTextOrNull(breed.getText().toString()));

        dog.setAge(HelperClass.getIntegerOrNull(age.getText().toString()));
        dog.setHeight(HelperClass.getIntegerOrNull(height.getText().toString()));
        dog.setWeight(HelperClass.getIntegerOrNull(weight.getText().toString()));

        // gender:
        if (female.isChecked()) {
            dog.setGender("F");
        } else if (male.isChecked()) {
            dog.setGender("M");
        }

        // TODO - add random color to dog:
        dog.setColor(addColorBasedOnIndex());

        final String dogColor = dog.getColor();

        Log.d(TAG, "save dog: " + dog.toString());
        // add dog to user:
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("users/" + FBAuth.getUserKey() + "/dogs/" + dogIndex)
            .setValue(dog.toMap(), new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(@Nullable DatabaseError databaseError,
                               @NonNull DatabaseReference databaseReference) {
            if (databaseError == null) {
                Log.d(TAG, "dog successfully added to user");

                // save notification token to dog
                // TODO - find better way to save color and token number
                db.getReference("dogs").child(dogKey).child("token").setValue(userToken);
                db.getReference("dogs").child(dogKey).child("color").setValue(dogColor);

                // close the activity
                finish();
            }
            }
        });
    }

    private String addColorBasedOnIndex() {
        switch (dogIndex % 4) {
            case 0:
                return "green";
            case 1:
                return "yellow";
            case 2:
                return "blue";
            case 3:
                return "red";
            default:
                return "white";
        }
    }

}
