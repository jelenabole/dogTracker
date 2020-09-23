package hr.tvz.trackmydog.activities;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.mappers.DogMapper;
import hr.tvz.trackmydog.models.forms.DogForm;
import hr.tvz.trackmydog.utils.LabelUtils;

public class DogDetailsEditActivity extends BaseActivity {

    private static final String TAG = "Dog Details Edit Activity";

    DogForm dog; // get the existing dog info

    @BindView(R.id.error) TextView error;
    @BindView(R.id.saveButton) Button saveButton;

    // @BindView(R.id.dogImage) ImageView dogImage;
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

        setContentView(R.layout.activity_dog_details_edit);
        ButterKnife.bind(this);

        // set listener to current user and get info:
        ViewModelProviders.of(this).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(this, currentUser -> {
                    if (currentUser != null) {
                        Integer dogIndex = getIntent().getIntExtra("dogIndex", -1);
                        dog = DogMapper.mapBasicDogToForm(currentUser.getDogs().get(dogIndex));
                        Log.d(TAG, "Dog index: " + dogIndex);

                        // set all fields to values
                        setFieldValues();
                    }
                });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDog();
            }
        });
    }

    private void setFieldValues() {
        Log.d(TAG, "set field values - prepare for edit");
        Log.d(TAG, "dog: " + dog.toString());

        name.setText(dog.getName());
        breed.setText(dog.getBreed());

        // can't return null for int:
        age.setText(LabelUtils.getStringForEdit(dog.getAge()));
        height.setText(LabelUtils.getStringForEdit(dog.getHeight()));
        weight.setText(LabelUtils.getStringForEdit(dog.getWeight()));

        if (dog.getGender() != null) {
            if (dog.getGender().equals("F")) {
                female.setChecked(true);
            } else if (dog.getGender().equals("M")) {
                male.setChecked(true);
            }
        }
    }

    // on button click
    private void saveDog() {
        error.setVisibility(View.GONE);

        // check if mandatory fields are entered:
        if (name.getText().length() < 1) {
            Log.w(TAG, "error - empty 'name' field!");
            error.setText(getString(R.string.error_empty_fields));
            error.setVisibility(View.VISIBLE);
            return;
        }
        Log.d(TAG, "save dog info: " + name.getText().toString());
        showProgressDialog();

        // get info from fields:
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

        // save dog:
        Log.d(TAG, "save dog: " + dog.toString());
        FirebaseDatabase.getInstance()
            .getReference("users/" + MyApplication.getUserKey() + "/dogs/" + dog.getIndex())
            .updateChildren(dog.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError,
                                       @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Log.d(TAG, "dog updated successfully");
                        finish();
                    } else {
                        hideProgressDialog();
                    }
                }
            });
    }
}