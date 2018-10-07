package hr.tvz.trackmydog.fragments;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.userModel.DogInfo;
import hr.tvz.trackmydog.utils.ResourceUtils;

public class DogThumbListAdapter extends RecyclerView.Adapter<DogThumbListAdapter.DogViewHolder> {

    // TODO - add color and image
    // TODO - calculate (from context ?) width/height to fit few (or to be 1/5 of the screen)
    // calculate the size and put the images

    // class for the individual views:
    class DogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.dogImage) SimpleDraweeView dogImage;
        final DogThumbListAdapter adapter;

        // constructor (since there is no deafult constructor for it) - bind views:
        private DogViewHolder(View itemView, DogThumbListAdapter adapter) {
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

            // TODO - start following the dog
        }
    }


    // TODO - other adapter stuff:
    private static String TAG = "Dog Thumbs Adapter";
    private List<DogInfo> dogList;
    private Context context;

    // constructor - inflate the view and set the list of items:
    DogThumbListAdapter(Context context) {
        this.context = context;
        dogList = new ArrayList<>();
    }

    @NonNull
    @Override
    public DogThumbListAdapter.DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_dog_tumb, parent, false);

        int paddingBetween = (int) context.getResources().getDimension(R.dimen.items_margin_between);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int thumbWidth = screenWidth / 6;


        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(thumbWidth, thumbWidth);
        params.setMargins(0, 0, paddingBetween, 0);

        itemView.setLayoutParams(params);
        return new DogViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull DogThumbListAdapter.DogViewHolder holder, int position) {
        Log.d(TAG, "bind view holder - dog found: " + dogList.get(position));

        // set dog info into holder views:
        setDogFrame(holder, dogList.get(position));
    }

    @Override
    public int getItemCount() {
        return dogList.size();
    }

    private void setDogFrame(DogThumbListAdapter.DogViewHolder holder, DogInfo dog) {
        int dogColor = ResourceUtils.getDogColor(dog.getColor(), context);

        // add dog photo (if exists):
        if (dog.getPhotoURL() !=  null) {
            Uri uri = Uri.parse(dog.getPhotoURL());
            holder.dogImage.setImageURI(uri);
        }

        holder.dogImage.setBackgroundColor(dogColor);
    }

    // add new data on change:
    void refreshData(List<DogInfo> dogs) {
        // TODO - workaround = remove all nulls from the list:
        // deleted dogs are left as nulls in fb array
        dogs.removeAll(Collections.singleton(null));
        // while(tourists.remove(null));
        this.dogList = dogs;
        this.notifyDataSetChanged();
    }
}