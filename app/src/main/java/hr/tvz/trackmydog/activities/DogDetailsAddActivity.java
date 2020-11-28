package hr.tvz.trackmydog.activities;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
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
import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.forms.DogForm;
import hr.tvz.trackmydog.utils.LabelUtils;

public class DogDetailsAddActivity extends BaseActivity {

    private static final String TAG = "Add New Dog Activity";

    private Integer dogIndex;
    private String dogKey;
    private String userToken;

    @BindView(R.id.error) TextView error;
    @BindView(R.id.saveButton) Button saveButton;

    // @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.addChipLayout) TextInputLayout addChipLayout;
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

        // reuse edit screen, with visible chip field
        setContentView(R.layout.activity_dog_details_edit);
        ButterKnife.bind(this);
        addChipLayout.setVisibility(View.VISIBLE);

        // set listener to current user and get info:
        new ViewModelProvider(this).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(this, currentUser -> {
                    if (currentUser != null) {
                        // get user notification token and dog index (number of dogs):
                        userToken = currentUser.getToken();
                        if (currentUser.getDogs() != null) {
                            dogIndex = currentUser.getDogs().size();
                        } else {
                            dogIndex = 0;
                        }
                        Log.d(TAG, "Dog index: " + dogIndex);
                    }
                });

        saveButton.setOnClickListener(v -> saveDog());
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
                   String code = dogSnaps.child("code").getValue(String.class);
                   assert code != null : "FB child code is null";

                   if (code.equals(dogCode)) {
                       // if dog is found:
                       found = true;
                       dogKey = dogSnaps.getKey();

                       // check if chip is already in use:
                       if (dogSnaps.child("notification/token").getValue() != null) {
                           Log.d(TAG, "chip already added to user");
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
                   error.setText(getString(R.string.error_wrong_chip_number));
                   error.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               // Toast.makeText(this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
               error.setText(getString(R.string.error_while_saving));
               error.setVisibility(View.VISIBLE);
           }
        });
    }

    // save dog and finish activity
    private void saveDogInfo() {
        Log.d(TAG, "save dog info - index: " + dogIndex);
        DogForm dog = new DogForm();

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
        // final String dogColor = dog.getColor();

        Log.d(TAG, "save dog: " + dog.toString());
        // add dog to user:
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("users/" + MyApplication.getUserKey() + "/dogs/" + dogIndex)
            .setValue(dog.toMap(), (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    Log.d(TAG, "dog successfully added to user");

                    // save notification token to dog
                    db.getReference("dogs").child(dogKey).child("notification/token").setValue(userToken);

                    // close the activity
                    finish();
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
