package hr.tvz.trackmydog.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.models.mappers.UserMapper;
import hr.tvz.trackmydog.models.forms.UserForm;
import hr.tvz.trackmydog.models.userModel.CurrentUser;
import hr.tvz.trackmydog.utils.LabelUtils;

public class UserDetailsEditActivity extends BaseActivity {

    private static final String TAG = "Edit User Details Activity";

    UserForm user;

    @BindView(R.id.error) TextView errorText;
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

        // Obtain a new or prior instance of CurrentUserViewModel from the ViewModelProviders utility class:
        CurrentUserViewModel currentUserViewModel =
                ViewModelProviders.of(this).get(CurrentUserViewModel.class);

        LiveData<CurrentUser> currentUserLiveData = currentUserViewModel.getCurrentUserLiveData();
        currentUserLiveData.observe(this, new Observer<CurrentUser>() {
            @Override
            public void onChanged(@Nullable CurrentUser currentUser) {
                if (currentUser != null) {
                    Log.d(TAG, "Current user data retrieved: " + currentUser);

                    // update the UI with values from the snapshot
                    user = UserMapper.mapCurretUserToForm(currentUser);
                    setFieldValues();
                }
            }
        });

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
        errorText.setVisibility(View.INVISIBLE);

        // check if mandatory fields are entered:
        if (name.getText().length() < 1) {
            Log.w(TAG, "error - empty 'name' field!");
            errorText.setText(getString(R.string.error_empty_fields));
            errorText.setVisibility(View.VISIBLE);
            return;
        }
        Log.d(TAG, "save user info: " + name.getText().toString());
        showProgressDialog();

        // get info from fields:
        user.setName(name.getText().toString());
        user.setCity(LabelUtils.getTextOrNull(city.getText().toString()));
        user.setPhoneNumber(LabelUtils.getTextOrNull(phoneNumber.getText().toString()));
        user.setGender(LabelUtils.getTextOrNull(gender.getText().toString()));

        // save user:
        UserService.saveUser(user, new ActivityCallback() {
            @Override
            public void closeIntent(boolean error) {
                if (!error) {
                    Log.d(TAG, "user updated successfully");
                    finish();
                } else {
                    Log.d(TAG, "error while saving user - DB error");
                    errorText.setText(getResources().getText(R.string.error_save_to_database));
                    errorText.setVisibility(View.VISIBLE);
                    hideProgressDialog();
                }
            }
        });
    }
}
