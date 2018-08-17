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
import hr.tvz.trackmydog.activities.ProfileDetailsActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";
    CurrentUser user;

    @BindView(R.id.name) TextView name;
    @BindView(R.id.location) TextView location;
    @BindView(R.id.editButton) ImageView editButton; // edit button
    @BindView(R.id.logoutButton) Button logoutButton;
    @BindView(R.id.addLocationButton) Button addLocationButton;

    // TODO - text views:
    @BindView(R.id.email) TextView email;
    @BindView(R.id.mobileNumber) TextView mobileNumber;
    @BindView(R.id.gender) TextView gender;
    @BindView(R.id.dob) TextView dob;

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
        // TODO - butterknife bind
        ButterKnife.bind(this, v);

        // TODO - get user info:
        user = FBAuth.getCurrentUserFB();

        // TODO - set all text views:
        setAllFields();

        // edit button listener:
        editButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                Log.d(TAG, "edit user: " + user.getDisplayName());
                Intent profileDetailsIntent = new Intent(getActivity(), ProfileDetailsActivity.class);
                startActivity(profileDetailsIntent);
            }
        });

        // logout button listener:
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                // TODO - finish doesnt clean the history
                Log.d(TAG, "logout user: " + user.getDisplayName());
                FBAuth.logoutUser(getActivity());
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

    // TODO - add all user info, delete code (?)
    private void setAllFields() {
        name.setText(HelperClass.getAsStringLabel(user.getDisplayName()));
        location.setText("Zagreb");

        email.setText(HelperClass.getAsStringLabel(user.getEmail()));
        mobileNumber.setText("099 1235 846");
        gender.setText(user.getCode());
        dob.setText("01/01/1990");
    }

}