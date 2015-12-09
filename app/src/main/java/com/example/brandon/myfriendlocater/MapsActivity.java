package com.example.brandon.myfriendlocater;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userLocalStore = new UserLocalStore(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMyLocationEnabled(true);

        User currentUser = userLocalStore.getLoggedInUser();
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchFriendLocationDataInBackground(currentUser, new GetUserCallback() {
            @Override
            public void doneLocationTask(ArrayList<Marker> returnedLocations) {
                for(int i = 0; i < returnedLocations.size(); i++) {
                    Marker friendLocation = returnedLocations.get(i);
                    LatLng friendLocationLatLng = new LatLng(Double.parseDouble(friendLocation.lng), Double.parseDouble(friendLocation.lat));
                    mMap.addMarker(new MarkerOptions().position(friendLocationLatLng).title(friendLocation.username));

                }
            }

            @Override
            public void done(User returnedUser) {

            }
        });

        mMap.addMarker(new MarkerOptions().position(new LatLng(45, -45)).title("Tom's Birthday, 10/24/15, 5:00PM").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_events:
                startActivity(new Intent(this, EventActivity.class));
                break;
            case R.id.action_map:
                return true;
            case R.id.action_main:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_friends:
                startActivity(new Intent(this, FriendsListActivity.class));
                break;
            case R.id.action_logout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                startActivity(new Intent(this, Login.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
