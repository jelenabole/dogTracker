package hr.tvz.trackmydog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.dogModel.Dog;
import hr.tvz.trackmydog.userModel.BasicDog;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class DogDetailsEditActivity extends AppCompatActivity {

    private static final String TAG = "Dog Details Edit Activity";

    // TODO - info from DbFlowApp:
    private BasicDog dog;

    // @BindView(R.id.dogImage) ImageView dogImage;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.breed) TextView breed;
    @BindView(R.id.age) TextView age;

    @BindView(R.id.height) TextView height;
    @BindView(R.id.weight) TextView weight;

    @BindView(R.id.gender) TextInputEditText gender;
    @BindView(R.id.dateOfBirth) TextInputEditText dateOfBirth;
    @BindView(R.id.saveButton) Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on Create");

        setContentView(R.layout.activity_dog_details_edit);
        ButterKnife.bind(this);

        int dogIndex = getIntent().getIntExtra("dogIndex", -1);
        dog = FBAuth.getCurrentUserFB().getDogs().get(dogIndex);

        // set all fields to values
        setFieldValues();
        saveButton.setOnClickListener(new View.OnClickListener() {
            // TODO - save function needed for firebase (and maybe local base)
            @Override
            public void onClick(View v) {
                System.out.println("changes saved - not imported");
                // save changes to firebase:
                // finish the activity
            }
        });
    };

    private void setFieldValues() {
        Log.d(TAG, "set field values - prepare for edit");

        Log.d(TAG, "dog: " + dog.toString());
        name.setText(getStringForEdit(dog.getName()));
        breed.setText(getStringForEdit(dog.getBreed()));
        age.setText(getStringForEdit(dog.getAge()));

        height.setText(getStringForEdit(dog.getHeight()));
        weight.setText(getStringForEdit(dog.getWeight()));
        dateOfBirth.setText(getStringForEdit(dog.getDateOfBirth()));
        gender.setText(getStringForEdit(dog.getGender()));
    }


    /* STRINGS FOR EDIT FIELDS */

    public String getStringForEdit(String str) {
        return str == null ? "" : str;
    }

    public String getStringForEdit(Integer str) {
        return str == null ? "" : str.toString();
    }

    public String getStringForEdit(Date str) {
        // TODO - format:
        return str == null ? "" : str.toString();
    }

}
