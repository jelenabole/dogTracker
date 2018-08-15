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
import hr.tvz.trackmydog.forms.DogBasicInfo;
import hr.tvz.trackmydog.userModel.BasicDog;

public class DogDetailsEditActivity extends AppCompatActivity {

    private static final String TAG = "Dog Details Edit Activity";

    // TODO - info from DbFlowApp:
    private BasicDog dog;
    private DatabaseReference dogRef;

    // @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.breed) TextView breed;
    @BindView(R.id.age) TextView age;

    @BindView(R.id.height) TextView height;
    @BindView(R.id.weight) TextView weight;

    // @BindView(R.id.dateOfBirth) TextInputEditText dateOfBirth;
    @BindView(R.id.saveButton) Button saveButton;

    @BindView(R.id.female) RadioButton female;
    @BindView(R.id.male) RadioButton male;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on Create");

        setContentView(R.layout.activity_dog_details_edit);
        ButterKnife.bind(this);

        int dogIndex = getIntent().getIntExtra("dogIndex", -1);
        // TODO - spojiti u jednu klasu (dog / user-dog)
        dog = FBAuth.getCurrentUserFB().getDogs().get(dogIndex);

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
        DogBasicInfo info = new DogBasicInfo();


        // check if all info is filled, save and quit
        if (HelperClass.isFieldEmpty(name.getText().toString())) {
            dog.setName(name.getText().toString());
        }
        if (HelperClass.isFieldEmpty(breed.getText().toString())) {
            dog.setBreed(breed.getText().toString());
        }
        if (HelperClass.isFieldEmpty(age.getText().toString())) {
            dog.setAge(Integer.valueOf(age.getText().toString()));
        }

        if (HelperClass.isFieldEmpty(height.getText().toString())) {
            dog.setHeight(Integer.valueOf(height.getText().toString()));
        }
        if (HelperClass.isFieldEmpty(weight.getText().toString())) {
            dog.setWeight(Integer.valueOf(weight.getText().toString()));
        }

        // gender:
        if (female.isChecked()) {
            dog.setGender("F");
        } else if (male.isChecked()) {
            dog.setGender("M");
        }

        // save dog:
        info.mapDog(dog);
        Log.d(TAG, info.toString());

        FirebaseDatabase.getInstance()
            .getReference("users/" + FBAuth.getUserKey() + "/dogs/" + dog.getIndex())
            .updateChildren(info.toMap(), new DatabaseReference.CompletionListener() {
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
