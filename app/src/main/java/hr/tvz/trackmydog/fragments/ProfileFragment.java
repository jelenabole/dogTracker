package hr.tvz.trackmydog.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.HelperClass;
import hr.tvz.trackmydog.activities.MapRangeActivity;
import hr.tvz.trackmydog.activities.UserDetailsEditActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";
    CurrentUser user;

    @BindView(R.id.editButton) ImageView editButton; // edit button
    @BindView(R.id.logoutButton) Button logoutButton;
    @BindView(R.id.addLocationButton) Button addLocationButton;

    // text views:
    @BindView(R.id.name) TextView name;
    @BindView(R.id.location) TextView location;
    @BindView(R.id.email) TextView email;
    @BindView(R.id.phoneNumber) TextView phoneNumber;
    @BindView(R.id.gender) TextView gender;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);

        // get user info:
        user = FBAuth.getCurrentUserFB();

        // set all text views:
        setAllFields();

        // edit button listener:
        editButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                Log.d(TAG, "edit user: " + user.getName());
                Intent profileDetailsIntent = new Intent(getActivity(), UserDetailsEditActivity.class);
                startActivity(profileDetailsIntent);
            }
        });

        // logout button listener:
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                Log.d(TAG, "logout user: " + user.getName());
                FBAuth.logoutUser();
            }
        });

        // add location (safe zone) button listener:
        addLocationButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                Log.d(TAG, "open AddLocation Activity");
                Intent addLocationIntent = new Intent(getActivity(), MapRangeActivity.class);
                startActivity(addLocationIntent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, " *** on Resume - refresh user");
        user = FBAuth.getCurrentUserFB();
        setAllFields();
    }

    // set all shown fields:
    private void setAllFields() {
        name.setText(HelperClass.getAsStringLabel(user.getName()));
        location.setText(HelperClass.getAsStringLabel(user.getCity()));
        email.setText(HelperClass.getAsStringLabel(user.getEmail()));

        phoneNumber.setText(HelperClass.getAsStringLabel(user.getPhoneNumber()));
        gender.setText(HelperClass.getAsStringLabel(user.getGender()));
    }

}