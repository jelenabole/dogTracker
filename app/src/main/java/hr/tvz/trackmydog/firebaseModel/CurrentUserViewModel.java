package hr.tvz.trackmydog.firebaseModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.firebase.UserQuery;

public class CurrentUserViewModel extends ViewModel {

    private static final DatabaseReference USER_REF =
            FirebaseDatabase.getInstance().getReference("/users/" + MyApplication.userKey);

    private final UserQuery liveData = new UserQuery(USER_REF);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
