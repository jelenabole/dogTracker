package hr.tvz.trackmydog.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.models.userModel.CurrentUser;
import hr.tvz.trackmydog.models.userModel.DogInfo;
import hr.tvz.trackmydog.utils.LabelUtils;
import hr.tvz.trackmydog.utils.ResourceUtils;

public class DogDetailsActivity extends BaseActivity {

    private static final String TAG = "Dog Details Activity";

    // TODO - Dog = with more info than user's DogInfo
    private DogInfo dog;

    @BindView(R.id.infoBanner) LinearLayout infoBanner;
    @BindView(R.id.dogImage) SimpleDraweeView dogImage;
    @BindView(R.id.sep1) View sep1;
    @BindView(R.id.sep2) View sep2;
    @BindView(R.id.nameText) TextView nameText;
    @BindView(R.id.breedText) TextView breedText;
    @BindView(R.id.ageText) TextView ageText;

    // basic info:
    @BindView(R.id.weightText) TextView weightText;
    @BindView(R.id.heightText) TextView heightText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create");

        setContentView(R.layout.activity_dog_details);
        ButterKnife.bind(this);
        hideProgressDialog();

        // get index of a dog:
        final Integer dogIndex = getIntent().getIntExtra("dogIndex", -1);

        // dogIndex = -1;
        if (dogIndex == -1) {
            finish();
        }

        Log.d(TAG, "show details of dog with index: " + dogIndex);

        // get user info and this dog:
        ViewModelProviders.of(this).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(this, new Observer<CurrentUser>() {
            @Override
            public void onChanged(@Nullable CurrentUser currentUser) {
                if (currentUser != null) {
                    if (currentUser.getDogs() != null) {
                        dog = currentUser.getDogs().get(dogIndex);
                        setDogInfo(dog);
                    }
                }
            }
        });
    };

    private void setDogInfo(DogInfo dog) {
        Log.d(TAG, "Get dog info: " + dog.getIndex());

        String dogName = LabelUtils.getAsStringLabel(dog.getName());
        String dogAge = LabelUtils.getAsStringLabel(dog.getAge()) + " yr";
        String dogBreed = LabelUtils.getAsStringLabel(dog.getBreed());

        // TODO - add some different info here (level of acitvity)
        /*
        String dogLastLocationTime = dog.getLocation() == null ? "no location detected" :
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(dog.getLocation().getTime()));
        */

        // set title bar:
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(dogName);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(ResourceUtils.getColor(dog.getColor(), this)));
        }

        nameText.setText(dogName);
        breedText.setText(dogBreed);
        ageText.setText(dogAge);

        // basic info:
        weightText.setText(LabelUtils.getAsStringLabel(dog.getWeight()));
        heightText.setText(LabelUtils.getAsStringLabel(dog.getHeight()));

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
        nameText.setTextColor(colorText);
        ageText.setTextColor(colorText);
        breedText.setTextColor(colorText);

        // label = dogColor
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
