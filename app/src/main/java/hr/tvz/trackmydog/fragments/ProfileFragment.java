package hr.tvz.trackmydog.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.FBAuth;
import hr.tvz.trackmydog.activities.ProfileDetailsActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class ProfileFragment extends Fragment {

    CurrentUser user;

    @BindView(R.id.name) TextView name;
    @BindView(R.id.location) TextView location;
    @BindView(R.id.editButton) ImageView editButton; // edit button
    @BindView(R.id.logoutButton) Button logoutButton;

    // TODO - text views:
    @BindView(R.id.email) TextView email;
    @BindView(R.id.mobileNumber) TextView mobileNumber;
    @BindView(R.id.gender) TextView gender;
    @BindView(R.id.dob) TextView dob;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
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
        System.out.println("Get user in fragment");
        System.out.println(user);

        // TODO - set all text views:
        name.setText(user.getDisplayName());
        location.setText("lokacija");

        email.setText(user.getEmail());
        mobileNumber.setText("099 1902 950");
        gender.setText(user.getCode());
        dob.setText("01/01/1990");

        // TODO - edit button listener:
        editButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
            System.out.println("change data");
            Intent profileDetailsIntent = new Intent(getActivity(), ProfileDetailsActivity.class);
            startActivity(profileDetailsIntent);
                  }
    });

        // TODO - logout button listener:
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
            // TODO - finish doesnt clean the history
                FBAuth.logoutUser(getActivity());
            }
        });

        return v;
    }

    // TODO - logout current user and return to the logout screen
    public void logout(View v) {
        System.out.println("LOGOUT user");
    }
}