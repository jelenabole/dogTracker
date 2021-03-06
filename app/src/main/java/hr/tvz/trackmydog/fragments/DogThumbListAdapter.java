package hr.tvz.trackmydog.fragments;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.generic.RoundingParams;
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

            // start following the dog
            fragment.showOnlyThisDog(index);
        }
    }


    private static String TAG = "Dog Thumbs Adapter";
    private List<DogInfo> dogList;
    private Context context;
    private MapFragment fragment;

    // constructor - inflate the view and set the list of items:
    DogThumbListAdapter(Context context, MapFragment fragment) {
        this.context = context;
        this.fragment = fragment;
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
        // workaround - fixed buttons going over linear layout (bottom border not full) - why 6px ???
        thumbWidth -= 6;


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

        // setting the border of an image:
        RoundingParams borderParams = new RoundingParams();
        borderParams.setCornersRadius(context.getResources().getDimension(R.dimen.border_radius))
                .setBorderWidth(context.getResources().getDimension(R.dimen.border_width))
                .setBorderColor(dogColor);
        holder.dogImage.getHierarchy().setRoundingParams(borderParams);
    }

    // add new data on change:
    void refreshData(List<DogInfo> dogs) {
        // workaround = remove all nulls from the list:
        // deleted dogs are left as nulls in fb array
        dogs.removeAll(Collections.singleton(null));
        // while(tourists.remove(null));
        this.dogList = dogs;
        this.notifyDataSetChanged();
    }
}