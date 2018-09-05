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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.mappers.DogMapper;
import hr.tvz.trackmydog.forms.DogForm;

public class DogDetailsEditActivity extends AppCompatActivity {

    private static final String TAG = "Dog Details Edit Activity";

    private Integer dogIndex;
    DogForm dog; // get the existing dog info

    // @BindView(R.id.error) TextView error;

    // @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.breed) TextView breed;
    @BindView(R.id.age) TextView age;
    @BindView(R.id.height) TextView height;
    @BindView(R.id.weight) TextView weight;
    @BindView(R.id.female) RadioButton female;
    @BindView(R.id.male) RadioButton male;

    // @BindView(R.id.dateOfBirth) TextInputEditText dateOfBirth;
    @BindView(R.id.saveButton) Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on Create");

        setContentView(R.layout.activity_dog_details_edit);
        ButterKnife.bind(this);

        dogIndex = getIntent().getIntExtra("dogIndex", -1);
        // TODO - spojiti u jednu klasu (dog / user-dog)
        dog = DogMapper.mapBasicDogToForm(FBAuth.getCurrentUserFB().getDogs().get(dogIndex));

        // set all fields to values
        setFieldValues();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDog();
            }
        });
    };

    private void setFieldValues() {
        Log.d(TAG, "set field values - prepare for edit");

        Log.d(TAG, "dog: " + dog.toString());
        name.setText(HelperClass.getStringForEdit(dog.getName()));
        breed.setText(HelperClass.getStringForEdit(dog.getBreed()));
        age.setText(HelperClass.getStringForEdit(dog.getAge()));

        height.setText(HelperClass.getStringForEdit(dog.getHeight()));
        weight.setText(HelperClass.getStringForEdit(dog.getWeight()));
        // dateOfBirth.setText(getStringForEdit(dog.getDateOfBirth()));

        if (dog.getGender() != null) {
            if (dog.getGender().equals("F")) {
                female.setChecked(true);
            } else if (dog.getGender().equals("M")) {
                male.setChecked(true);
            }
        }
    }

    // save dog and finish activity
    private void saveDog() {
        Log.d(TAG, "save dog info");

        // save dog:
        // TODO - add dog index (needed ??):
        // refresh fields (only the ones that are changed (that can be changed by user)
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

        // save dog:
        Log.d(TAG, "save dog: " + dog.toString());

        FirebaseDatabase.getInstance()
            .getReference("users/" + FBAuth.getUserKey() + "/dogs/" + dog.getIndex())
            .updateChildren(dog.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError,
                                       @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Log.d(TAG, "dog updated successfully");
                        finish();
                    }
                }
            });
    }

}