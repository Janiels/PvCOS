package hds.pvcos;

import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ChildEventListener usersListener;
    private Handler handler;
    private Runnable updateUserPositionRunnable;
    private ArrayList<Marker> markers = new ArrayList<>();

    private String getEmail() {
        String email = ((PvcApp)getApplication()).getEmail();
        return email;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        updateUserPositionRunnable = new Runnable() {
            @Override
            public void run() {
                updateUserPosition();
                handler.postDelayed(updateUserPositionRunnable, 5000);
            }
        };
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    private void updateUserPosition() {
        Location location = mMap.getMyLocation();
        if (location == null)
            return;

        Firebase server = ((PvcApp)getApplication()).getServer();

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        String locationStr = String.format(Locale.US, "%f,%f", lat, lng);

        String email = getEmail();
        String user = email.substring(email.indexOf('@'))
                            .replace('.', ' ')
                            .replace('#', ' ')
                            .replace('$', ' ')
                            .replace('[', ' ')
                            .replace(']', ' ');

        server.child("users").child(user).setValue(locationStr);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Firebase server = ((PvcApp)getApplication()).getServer();
        server.removeEventListener(usersListener);

        handler.removeCallbacks(updateUserPositionRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        Firebase server = ((PvcApp)getApplication()).getServer();

        usersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChangeOrAddMarkerForUser(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChangeOrAddMarkerForUser(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };

        server.child("users").addChildEventListener(usersListener);

        updateUserPositionRunnable.run();
    }

    private void ChangeOrAddMarkerForUser(DataSnapshot user) {
        String name = (String)user.getKey();
        String location = (String)user.getValue();

        if (name.equals(getEmail()))
            return;

        Marker foundMarker = null;

        for (Marker marker : markers) {
            if (marker.getTitle().equals(name)) {
                foundMarker = marker;
                break;
            }
        }

        double lat, lng;
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            String[] split = location.split(",");

            lat = format.parse(split[0]).doubleValue();
            lng  = format.parse(split[1]).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        if (foundMarker == null) {
            Marker newMarker = mMap.addMarker(new MarkerOptions()
                    .title(name)
                    .position(new LatLng(lat, lng))
                    .draggable(false));

            markers.add(newMarker);
        } else {
            foundMarker.setPosition(new LatLng(lat, lng));
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
    }
}
