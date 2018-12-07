package hr.tvz.trackmydog.firebaseModel;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CurrentUserQueryLiveData extends LiveData<DataSnapshot> {

    private static final String TAG = "FB Query - User";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public CurrentUserQueryLiveData(Query query) {
        this.query = query;
    }

    public CurrentUserQueryLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    /* whenever activty/fragment associated with this is on, this will be active */

    @Override
    protected void onActive() {
        Log.d(TAG, "onActive");
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive");
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
