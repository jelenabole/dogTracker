package hr.tvz.trackmydog;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.dogModel.Dog;
import hr.tvz.trackmydog.localDB.DbFlowApp;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class ProfileDetailsActivity extends AppCompatActivity {

    // TODO - info from DbFlowApp:
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
        System.out.println("DogDetails - On Create");

        // TODO - need to initialize Fresco before the contectView and using it:
        setContentView(R.layout.activity_profile_details);
        ButterKnife.bind(this);

        // TODO - get user and get dog index = get their info:
        user = ((DbFlowApp) getApplication()).getFirebaseUser();
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
