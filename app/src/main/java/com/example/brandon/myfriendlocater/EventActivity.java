package com.example.brandon.myfriendlocater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity implements View.OnClickListener {
    Button bAddEvent;
    UserLocalStore userLocalStore;
    EditText etEventTitle, etEventDate, etEventTime, etEventLatitude, etEventLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        userLocalStore = new UserLocalStore(this);
        bAddEvent = (Button) findViewById(R.id.bAddEvent);
        bAddEvent.setOnClickListener(this);
        etEventTitle = (EditText) findViewById(R.id.etEventTitle);
        etEventDate = (EditText) findViewById(R.id.etEventDate);
        etEventTime = (EditText) findViewById(R.id.etEventTime);
        etEventLatitude = (EditText) findViewById(R.id.etEventLatitude);
        etEventLongitude = (EditText) findViewById(R.id.etEventLongitude);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bAddEvent:
                User currentUser = userLocalStore.getLoggedInUser();
                ServerRequests serverRequests = new ServerRequests(this);
                serverRequests.storeEventDataInBackground(currentUser, String.valueOf(etEventTitle.getText()),
                        String.valueOf(etEventDate.getText()), String.valueOf(etEventTime.getText()),
                        String.valueOf(etEventLatitude.getText()), String.valueOf(etEventLongitude.getText()),
                        new GetUserCallback() {

                    @Override
                    public void done(User returnedUser) {

                    }

                    @Override
                    public void doneLocationTask(ArrayList<Marker> returnedLocations) {

                    }
                });
                Toast.makeText(EventActivity.this, "Event Added", Toast.LENGTH_SHORT).show();
                etEventTitle.setText("");
                etEventDate.setText("");
                etEventTime.setText("");
                etEventLatitude.setText("");
                etEventLongitude.setText("");
                break;
        }

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
                return true;

            case R.id.action_map:
                startActivity(new Intent(this, MapsActivity.class));
                break;
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
