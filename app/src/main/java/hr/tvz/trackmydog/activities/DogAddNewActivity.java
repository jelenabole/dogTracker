package hr.tvz.trackmydog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.firebase.FBAuth;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.forms.DogForm;
import hr.tvz.trackmydog.utils.LabelUtils;

public class DogAddNewActivity extends BaseActivity {

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

        // get user notification token and dog index (number of dogs):
        userToken = FBAuth.getCurrentUserFB().getToken();
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
    }

    // on button clicked
    private void saveDog() {
        error.setVisibility(View.GONE);

        // check if mandatory fields are entered:
        if (chipNumber.getText().toString().length() < 2 || name.getText().length() < 1) {
            Log.w(TAG, "error - empty fields: ("
                    + chipNumber.getText() + ") - (" + name.getText() + ")");
            error.setText(getString(R.string.error_empty_fields));
            error.setVisibility(View.VISIBLE);
            return;
        }

        final String dogCode = chipNumber.getText().toString();
        Log.d(TAG, "get dog from firebase - by code: " + dogCode);
        showProgressDialog();

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
                           hideProgressDialog();
                           error.setText(getString(R.string.error_chip_has_user));
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
                   hideProgressDialog();
                   error.setText(getString(R.string.error_wrong_chip_number));
                   error.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               // Toast.makeText(this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
               hideProgressDialog();
               error.setText(getString(R.string.error_while_saving));
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
        dog.setBreed(LabelUtils.getTextOrNull(breed.getText().toString()));

        dog.setAge(LabelUtils.getIntegerOrNull(age.getText().toString()));
        dog.setHeight(LabelUtils.getIntegerOrNull(height.getText().toString()));
        dog.setWeight(LabelUtils.getIntegerOrNull(weight.getText().toString()));

        // gender:
        if (female.isChecked()) {
            dog.setGender("F");
        } else if (male.isChecked()) {
            dog.setGender("M");
        }

        // add random color to dog:
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

                // close the activity
                finish();
            }
            }
        });
    }

    // TODO - move to helper class:
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
