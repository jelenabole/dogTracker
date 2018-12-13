package hr.tvz.trackmydog.activities;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.firebaseServices.DogService;
import hr.tvz.trackmydog.firebaseServices.SpinnerCallback;
import hr.tvz.trackmydog.models.forms.DogSettingsForm;
import hr.tvz.trackmydog.models.userModel.CurrentUser;
import hr.tvz.trackmydog.models.userModel.DogInfo;
import hr.tvz.trackmydog.utils.DesignUtils;
import hr.tvz.trackmydog.utils.LabelUtils;
import hr.tvz.trackmydog.utils.ResourceUtils;

public class DogDetailsActivity extends BaseActivity {

    private static final String TAG = "Dog Details Activity";

    // model:
    private DogInfo dog;
    private CurrentUser user;
    private DogSettingsForm dogSettings;

    // header info:
    @BindView(R.id.infoBanner) LinearLayout infoBanner;
    @BindView(R.id.dogImage) SimpleDraweeView dogImage;
    @BindView(R.id.sep1) View sep1;
    @BindView(R.id.sep2) View sep2;
    @BindView(R.id.kilometersText) TextView kilometersText;
    @BindView(R.id.breedText) TextView breedText;
    @BindView(R.id.ageText) TextView ageText;

    // basic info:
    @BindView(R.id.breed) TextView breed;
    @BindView(R.id.age) TextView age;
    @BindView(R.id.height) TextView height;
    @BindView(R.id.weight) TextView weight;
    @BindView(R.id.gender) TextView gender;

    @BindView(R.id.scrollView) ScrollView scrollView;

    // linear layouts for options (popup windows):
    @BindView(R.id.locationLayout) LinearLayout locationLayout;
    @BindView(R.id.intervalLayout) LinearLayout intervalLayout;
    @BindView(R.id.location) TextView location;
    @BindView(R.id.interval) TextView interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create");
        setContentView(R.layout.activity_dog_details);
        ButterKnife.bind(this);
        hideProgressDialog();

        /* animations */

        getWindow().setAllowReturnTransitionOverlap(true);
        getWindow().setAllowEnterTransitionOverlap(true);

        // TODO - set for the fresco transitions:
        getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(
                ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
        // not needed:
        getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(
                ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
        // needed for returning:
        getWindow().setSharedElementExitTransition(DraweeTransition.createTransitionSet(
                ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));


        /* model */

        // get index of a dog:
        final Integer dogIndex = getIntent().getIntExtra("dogIndex", -1);

        // dogIndex = -1;
        if (dogIndex == -1) {
            finish();
        }

        Log.d(TAG, "show details of dog with index: " + dogIndex);

        // TODO - change model to get the user from previous activity
        // get user info and this dog:
        ViewModelProviders.of(this).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(this, currentUser -> {
                    if (currentUser != null) {
                        user = currentUser;
                        if (currentUser.getDogs() != null) {
                            dog = currentUser.getDogs().get(dogIndex);
                            setDogSettingsListener();
                            setDogInfo(dog);
                        }
                    }
                });

        /* options - with popup windows onClick */

        locationLayout.setOnClickListener(view -> {
            List<String> data = user.getSafeZones().values().stream()
                    .map(place -> place.getName())
                    .collect(Collectors.toList());

            openPopupWindow(data, true, string -> {
                // TODO - check the first (placeholder):
                if (!string.equals("-")) {
                    // TODO - find data to save (from the user.safeZones)
                    dogSettings.setLocationName(string);
                    DogService.saveSettings(dogSettings, dog.getKey());
                } else {
                    dogSettings.deleteSafeZone();
                    DogService.saveSettings(dogSettings, dog.getKey());
                }
            });
        });

        intervalLayout.setOnClickListener(view -> {
            List<String> data = Arrays.asList(
                    getResources().getStringArray(R.array.location_intervals_array));

            // open popup with data and callback
            openPopupWindow(data, false, number -> {
                dogSettings.setInterval(Integer.parseInt(number));
                DogService.saveSettings(dogSettings, dog.getKey());
            });
        });
    }

    private void setDogSettingsListener() {
        Log.d(TAG, "get dog settings from firebase");

        // get dogs settings:
        FirebaseDatabase.getInstance().getReference("dogs/" + dog.getKey() + "/settings")
            .addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dogSettings = dataSnapshot.getValue(DogSettingsForm.class);
                        Log.d(TAG, "dog settings changed: " + dogSettings);

                        setDogSettings();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error - get dog settings (FB): " + databaseError.getCode());
                    }
                });
    }

    private void setDogSettings() {
        Log.d(TAG, "Get dog settings: " + dog.getIndex());

        // possible nulls:
        if (dogSettings.getLocationName() != null) {
            String dogLocation = dogSettings.getLocationName();
            location.setText(dogLocation);
            location.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        } else {
            location.setText("");
            location.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dash, 0);
        }

        // non-null items:
        String dogInterval = dogSettings.getInterval() + getResources().getString(R.string.seconds);
        interval.setText(dogInterval);
        interval.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
    }

    private void openPopupWindow(final List<String> data, boolean firstPlaceholder,
                                 final SpinnerCallback callback) {
        Log.d(TAG, "open popup window");
        if (firstPlaceholder) {
            data.add(0, "-");
        }

        // inflate popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null, false);

        // wheel picker:
        final WheelPicker wheelPicker = popupView.findViewById(R.id.wheelPicker);
        wheelPicker.setData(data);

        // TODO - set height = set by xml (???)
        // get size of a screen (calc height for the popup):
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int) (displayMetrics.heightPixels * 0.40);

        // popup window and button actions:
        // contentView, width, height, focusable (false)
        final PopupWindow popup = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, height, true);
        popup.setAnimationStyle(R.style.popupAnimations);
        popup.showAtLocation(scrollView, Gravity.BOTTOM, 0, 0);
        DesignUtils.dimBackground(popup);

        popupView.findViewById(R.id.popupCancelButton)
            .setOnClickListener(view -> popup.dismiss());

        popupView.findViewById(R.id.popupDoneButton)
            .setOnClickListener(view -> {
                Log.d(TAG, "spinner - picked: " + data.get(wheelPicker.getCurrentItemPosition()));
                callback.save(data.get(wheelPicker.getCurrentItemPosition()));
                popup.dismiss();
            });
    }

    private void setDogInfo(DogInfo dog) {
        Log.d(TAG, "Get dog info: " + dog.getIndex());

        String dogName = LabelUtils.getAsStringLabel(dog.getName());
        String dogAge = LabelUtils.getAsStringLabel(dog.getAge()) +
                getResources().getString(R.string.years);
        String dogBreed = LabelUtils.getAsStringLabel(dog.getBreed());
        String dogWeight = LabelUtils.getAsStringLabel(dog.getWeight()) +
                getResources().getString(R.string.kilograms);
        String dogHeight = LabelUtils.getAsStringLabel(dog.getHeight()) +
                getResources().getString(R.string.centimeters);

        String dogKilometers = "10.2" + getResources().getString(R.string.kilometers);

        // TODO - add some different info here (level of acitvity)
        /*
        String dogLastLocationTime = dog.getLocation() == null ? "no location detected" :
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(dog.getLocation().getTime()));
        */

        // set title bar:
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(dogName);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(ResourceUtils.getDogColor(dog.getColor(), this)));
        }

        // banner info:
        kilometersText.setText(dogKilometers);
        breedText.setText(dogBreed);
        ageText.setText(dogAge);

        // basic info:
        breed.setText(dogBreed);
        age.setText(dogAge);
        weight.setText(dogWeight);
        height.setText(dogHeight);
        gender.setText(LabelUtils.getAsStringLabel(dog.getGender()));

        changeColorTo(dog.getColor());

        // set dog image:
        if (dog.getPhotoURL() !=  null) {
            dogImage.setImageURI(Uri.parse(dog.getPhotoURL()));
        }
    }

    private void changeColorTo(String colorName) {
        int color = ResourceUtils.getColorFromRes(colorName, null,
                getResources(), this);
        int colorOpaque = ResourceUtils.getColorFromRes(colorName, "_op",
                getResources(), this);
        int colorText = ResourceUtils.getColorFromRes(colorName,"_text",
                getResources(), this);

        // BANNER - background and separators:
        infoBanner.setBackgroundColor(color);
        sep1.setBackgroundColor(colorText);
        sep2.setBackgroundColor(colorText);

        // BANNER - text color:
        kilometersText.setTextColor(colorText);
        ageText.setTextColor(colorText);
        breedText.setTextColor(colorText);
    }

    /* Edit dog info menu */

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit) {
            Log.d(TAG, "edit dog info: " + dog.getIndex());
            Intent intent = new Intent(this, DogDetailsEditActivity.class);
            intent.putExtra("dogIndex", dog.getIndex());
            startActivityForResult(intent, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "returned from editing dog info");
        if (resultCode == RESULT_OK && requestCode == 0) {
            Toast.makeText(this, "Dog info Saved", Toast.LENGTH_SHORT).show();
        }
    }
}
