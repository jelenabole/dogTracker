package hr.tvz.trackmydog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.forms.UserForm;
import hr.tvz.trackmydog.mappers.UserMapper;

public class UserDetailsEditActivity extends BaseActivity {

    private static final String TAG = "Edit User Details Activity";

    private String userLink;
    private UserForm user;

    @BindView(R.id.error) TextView error;
    @BindView(R.id.saveButton) Button saveButton;

    @BindView(R.id.name) TextInputEditText name;
    @BindView(R.id.city) TextInputEditText city;
    @BindView(R.id.phoneNumber) TextInputEditText phoneNumber;
    @BindView(R.id.gender) TextInputEditText gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create");

        setContentView(R.layout.activity_user_details_edit);
        ButterKnife.bind(this);

        userLink = "users/" + FBAuth.getCurrentUserFB().getKey();
        Log.d(TAG, "user link: " + userLink);
        user = UserMapper.mapCurretUserToForm(FBAuth.getCurrentUserFB());

        // set all fields:
        setFieldValues();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });
    }

    private void setFieldValues() {
        Log.d(TAG, "set field values - prepare for edit");

        name.setText(user.getName());
        city.setText(user.getCity());
        phoneNumber.setText(user.getPhoneNumber());
        gender.setText(user.getGender());
    }

    public void saveUser() {
        error.setVisibility(View.GONE);

        // check if mandatory fields are entered:
        if (name.getText().length() < 1) {
            Log.w(TAG, "error - empty 'name' field!");
            error.setText(getString(R.string.error_empty_fields));
            error.setVisibility(View.VISIBLE);
            return;
        }
        Log.d(TAG, "save user info: " + name.getText().toString());
        showProgressDialog();

        // get info from fields:
        user.setName(name.getText().toString());
        user.setCity(HelperClass.getTextOrNull(city.getText().toString()));
        user.setPhoneNumber(HelperClass.getTextOrNull(phoneNumber.getText().toString()));
        user.setGender(HelperClass.getTextOrNull(gender.getText().toString()));

        // save user:
        Log.d(TAG, "save user: " + user.toString());
        FirebaseDatabase.getInstance().getReference(userLink)
            .updateChildren(user.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError,
                                       @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Log.d(TAG, "user updated successfully");
                        finish();
                    } else {
                        hideProgressDialog();
                    }
                }
            });
    }
}
