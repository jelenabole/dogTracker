package hr.tvz.trackmydog.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.firebaseServices.ActivityCallback;
import hr.tvz.trackmydog.firebaseServices.UserService;
import hr.tvz.trackmydog.models.forms.UserForm;
import hr.tvz.trackmydog.utils.LabelUtils;

public class UserAddNewActivity extends BaseActivity {

    private static final String TAG = "Add User Activity";

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

        setContentView(R.layout.activity_user_add);
        ButterKnife.bind(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });
    }

    // on button clicked
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

        UserForm user = new UserForm();

        user.setName(name.getText().toString());
        user.setCity(LabelUtils.getTextOrNull(city.getText().toString()));
        user.setPhoneNumber(LabelUtils.getTextOrNull(phoneNumber.getText().toString()));
        user.setGender(LabelUtils.getTextOrNull(gender.getText().toString()));

        // save user:
        UserService.saveUser(user, new ActivityCallback() {
            @Override
            public void closeIntent(boolean error) {
                if (!error) {
                    Log.d(TAG, "user saved successfully");
                    finish();
                } else {
                    hideProgressDialog();
                }
            }
        });
    }
}