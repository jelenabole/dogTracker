package hr.tvz.trackmydog.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class ProfileDetailsActivity extends AppCompatActivity {

    // TODO - info from MyApplication:
    private String userLink;
    private CurrentUser user;

    @BindView(R.id.name) TextInputEditText name;
    @BindView(R.id.email) TextInputEditText email;
    @BindView(R.id.mobileNumber) TextInputEditText mobileNumber;
    @BindView(R.id.gender) TextInputEditText gender;
    @BindView(R.id.dateOfBirth) TextInputEditText dateOfBirth;
    @BindView(R.id.saveButton) Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Profile Details - On Create");

        // TODO - need to initialize Fresco before the contectView and using it:
        setContentView(R.layout.activity_profile_details);
        ButterKnife.bind(this);

        // TODO - get user and get dog index = get their info:
        user = FBAuth.getCurrentUserFB();
        userLink = "users/" + user.getKey();

        // TODO - set all fields to values
        setFieldValues();
    };

    private void setFieldValues() {
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        mobileNumber.setText("099 1902 950");
        gender.setText("F");
        dateOfBirth.setText("01.01.1990.");
    }

    // TODO - save function needed for firebase (and maybe local base)
    public void saveChanges(View v) {
        System.out.println("changes saved - not imported");
    }

}
