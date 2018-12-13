package hr.tvz.trackmydog.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.activities.DogDetailsActivity;
import hr.tvz.trackmydog.models.userModel.DogInfo;
import hr.tvz.trackmydog.utils.ResourceUtils;
import hr.tvz.trackmydog.utils.TimeUtils;

public class DogInfoListAdapter extends RecyclerView.Adapter<DogInfoListAdapter.DogViewHolder> {

    // class for the individual views:
    class DogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.dogImage) SimpleDraweeView dogImage;
        @BindView(R.id.nameText) TextView nameText;
        @BindView(R.id.breedText) TextView breedText;
        @BindView(R.id.locationText) TextView locationText;
        final DogInfoListAdapter adapter;

        // constructor (since there is no deafult constructor for it) - bind views:
        private DogViewHolder(View itemView, DogInfoListAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get the position of the item that was clicked.
            int position = getLayoutPosition();
            int index = dogList.get(position).getIndex();

            Log.d(TAG, "dog ViewHolder clicked: " + position + " - dog index: " + index);

            // start intent for that dog:
            Intent dogDetailsIntent = new Intent(context, DogDetailsActivity.class);
            dogDetailsIntent.putExtra("dogIndex", index);

            // TODO - add transition name:
            // v.setTransitionName("transitionDogImage");
            View image = ((LinearLayout)((LinearLayout) v).getChildAt(0)).getChildAt(0);

            // System.out.println(((LinearLayout)((LinearLayout) v).getChildAt(0)).getChildAt(0));

            // TODO - set with animation: (and shared element view and common transition name)
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    (Activity) context, image, "transitionDogImage");
            context.startActivity(dogDetailsIntent, options.toBundle());

            // context.startActivity(dogDetailsIntent, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());

            // context.startActivity(dogDetailsIntent);
        }
    }

    private static String TAG = "Dog List Adapter";
    private List<DogInfo> dogList;
    private LayoutInflater layoutInflater;
    private Context context;

    // constructor - inflate the view and set the list of items:
    DogInfoListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        dogList = new ArrayList<>();
    }

    @NonNull
    @Override
    public DogInfoListAdapter.DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_dog_info, parent, false);
        return new DogViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull DogInfoListAdapter.DogViewHolder holder, int position) {
        Log.d(TAG, "bind view holder - dog found: " + dogList.get(position));

        // set dog info into holder views:
        setDogFrame(holder, dogList.get(position));
    }

    @Override
    public int getItemCount() {
        return dogList.size();
    }

    private void setDogFrame(DogInfoListAdapter.DogViewHolder holder, DogInfo dog) {
        // handle null text and labels:
        String dogName = dog.getName() == null ? "-- unknown --" : dog.getName();
        if (dog.getAge() != null) {
            dogName += " (" + dog.getAge() + ")";
        }
        String dogBreed = dog.getBreed() == null ? "-- unknown --" : dog.getBreed();
        String dogLastLocationTime = TimeUtils.getLastLocationTime(null);
        int dogColor = ResourceUtils.getDogColor(dog.getColor(), context);

        // add dog photo (if exists):
        if (dog.getPhotoURL() !=  null) {
            Uri uri = Uri.parse(dog.getPhotoURL());
            holder.dogImage.setImageURI(uri);
        }

        // setting the border of an image:
        RoundingParams borderParams = new RoundingParams();
        borderParams.setCornersRadius(context.getResources().getDimension(R.dimen.border_radius))
                .setBorderWidth(context.getResources().getDimension(R.dimen.border_width))
                .setBorderColor(dogColor);

        holder.dogImage.getHierarchy().setRoundingParams(borderParams);

        holder.nameText.setTextColor(dogColor);
        holder.nameText.setText(dogName);
        holder.breedText.setText(dogBreed);
        holder.locationText.setText(dogLastLocationTime);
    }

    // add new data on change:
    void refreshData(List<DogInfo> dogs) {
        dogs.removeAll(Collections.singleton(null));
        this.dogList = dogs;
        this.notifyDataSetChanged();
    }
}