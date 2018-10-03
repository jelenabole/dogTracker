package hr.tvz.trackmydog.firebaseModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.firebaseQueries.CurrentUserQueryLiveData;
import hr.tvz.trackmydog.models.userModel.CurrentUser;

public class CurrentUserViewModel extends ViewModel {

    private static final DatabaseReference USER_REF =
            FirebaseDatabase.getInstance().getReference("/users/" + MyApplication.userKey);

    private final CurrentUserQueryLiveData liveData = new CurrentUserQueryLiveData(USER_REF);


    /* added later = automatically map to current user */

    /*
    private final LiveData<CurrentUser> currentUserLiveData = Transformations.map(liveData, new Deserializer());

    private class Deserializer implements Function<DataSnapshot, CurrentUser> {
        @Override
        public CurrentUser apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(CurrentUser.class);
        }
    }

    @NonNull
    public LiveData<CurrentUser> getCurrentUserLiveData() {
        return currentUserLiveData;
    }
    */


    // TODO - try using MediatorLiveData (but needs Executor pool for threads)
    /* third time = adding asyncronous transformation */

    private final MediatorLiveData<CurrentUser> currentUserLiveData = new MediatorLiveData<>();

    public CurrentUserViewModel() {
        // Set up the MediatorLiveData to convert DataSnapshot objects into HotStock objects
        currentUserLiveData.addSource(liveData, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable final DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            currentUserLiveData.postValue(dataSnapshot.getValue(CurrentUser.class));
                        }
                    }).start();
                } else {
                    currentUserLiveData.setValue(null);
                }
            }
        });
    }

    @NonNull
    public LiveData<CurrentUser> getCurrentUserLiveData() {
        return currentUserLiveData;
    }


    /* old function = gets live data instead of the deserialized current user */

    /*
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
    */
}
