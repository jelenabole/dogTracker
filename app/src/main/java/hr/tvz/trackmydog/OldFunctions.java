package hr.tvz.trackmydog;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hr.tvz.trackmydog.fragments.MapFragment;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class OldFunctions extends BaseActivity {


    CurrentUser user = null;
    String userEmail = "jelenabole@gmail.com";
    // TODO - funkcija prebacena u App (MyCallback)
    // TODO - get all users (single event):
    // find current user by email
    protected void findCurrentUserInfo(String email) {
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnaps : dataSnapshot.getChildren()) {
                            // TODO - find user by email:
                            if (userSnaps.child("email").getValue().equals(userEmail)) {
                                // get the user and the key:
                                user = userSnaps.getValue(CurrentUser.class);
                                user.setKey(userSnaps.getKey());
                            /*
                            System.out.println("START *******************");
                            System.out.println(user);
                            System.out.println("END *********************");
                            break;
                            */
                                setListenerToCurrentUser();

                                // TODO - display the first fragment after the user is referenced:
                                // Manually displaying the first fragment - one time only
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, MapFragment.newInstance());

                                // TODO - error = Cannot perform this action after onSaveInstanceState
                                transaction.commit();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        // TODO - cancel the change things
    }

    private DatabaseReference userRef;
    // TODO - get the current user by the Key (from local DB):
    protected void setListenerToCurrentUser() {
        System.out.println("get the USER info: " + user.getKey());

        if (user.getKey() != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users/" + user.getKey());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // System.out.println(dataSnapshot);

                    // TODO - namapirati tog usera na svaku promjenu? potrebno??
                    user = dataSnapshot.getValue(CurrentUser.class);
                    user.setKey(dataSnapshot.getKey());

                    System.out.println(user);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }


        // TODO -ako je potrebno još nešto napraviti, i ugasiti gornji listener
        // TODO - postaviti listener samo na tog usera:
    }



    // TODO - with only one marker:
    /*
    private void changeLocationForDogOld(Dog dog) {
        System.out.println("GET DOG LOCATION ****");
        // TODO - get marker for the dog, change the location:
        // Add a marker in Sydney and move the camera

        Drawable myIcon = getContext().getResources().getDrawable(R.drawable.paw, getContext().getTheme());
        myIcon.setColorFilter( 0xffff0000, PorterDuff.Mode.MULTIPLY);

        // TODO - get the icon:
        int icon = getIcon(dog.getColor());

        // TODO - add position and the marker:
        LatLng position = new LatLng(dog.getLocation().getLatitude(), dog.getLocation().getLongitude());
        if (marker != null) {
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions()
                .position(position)
                .alpha(1.0f)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.paw))
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .title(dog.getName())
        ); // TODO - add zIndex (higher - top)


        // map.addMarker(new MarkerOptions().position(position).title(dog.getName()));

        // TODO - do this only first time:
        if (start) {
            start = false;
            map.moveCamera(CameraUpdateFactory.newLatLng(position));
            // zoom level is from 2.0 to 21.0 - not all locations have tiles at or near max zoom:
            map.animateCamera(CameraUpdateFactory.zoomTo( 17.0f ) );
        }
    }
    */

}

