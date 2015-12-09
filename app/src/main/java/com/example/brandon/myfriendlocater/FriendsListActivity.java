package com.example.brandon.myfriendlocater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity implements View.OnClickListener{
    Button bAddFriend, bRemoveFriend;
    EditText etNewFriend;
    UserLocalStore userLocalStore;
    String selectedFriend = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        userLocalStore = new UserLocalStore(this);
        bAddFriend = (Button) findViewById(R.id.bAddFriend);
        bRemoveFriend = (Button) findViewById(R.id.bRemoveFriend);
        etNewFriend = (EditText) findViewById(R.id.etNewFriend);
        bAddFriend.setOnClickListener(this);
        bRemoveFriend.setOnClickListener(this);
        User currentUser = userLocalStore.getLoggedInUser();
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserFriendListDataInBackground(currentUser, new GetFriendListCallback() {


            @Override
            public void doneFriendListTask(ArrayList<String> returnedFriendList) {

                String[] friends = new String[returnedFriendList.size()];
                friends = returnedFriendList.toArray(friends);

                ListAdapter listAdapter = new ArrayAdapter<String>(FriendsListActivity.this, android.R.layout.simple_selectable_list_item, friends);
                ListView friendsListView = (ListView) findViewById(R.id.friendsListView);

                friendsListView.setAdapter(listAdapter);
                friendsListView.setChoiceMode(1);
                friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        //String friendPicked = "You selected " + String.valueOf(adapterView.getItemAtPosition(position));
                        //Toast.makeText(FriendsListActivity.this, friendPicked, Toast.LENGTH_SHORT).show();
                        selectedFriend = String.valueOf(adapterView.getItemAtPosition(position));
                    }
                });
            }
        });


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
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.action_main:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_friends:
                return true;
            case R.id.action_logout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                startActivity(new Intent(this, Login.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bAddFriend:

                User currentUser = userLocalStore.getLoggedInUser();
                ServerRequests serverRequests = new ServerRequests(this);
                serverRequests.storeFriendDataInBackground(currentUser, String.valueOf(etNewFriend.getText()), new GetUserCallback() {

                    @Override
                    public void done(User returnedUser) {

                    }

                    @Override
                    public void doneLocationTask(ArrayList<Marker> returnedLocations) {

                    }
                });

                Toast.makeText(FriendsListActivity.this, "Friend Added", Toast.LENGTH_SHORT).show();
                etNewFriend.setText("");
                break;
            case R.id.bRemoveFriend:

                if(selectedFriend != "")
                {
                    currentUser = userLocalStore.getLoggedInUser();
                    serverRequests = new ServerRequests(this);
                    serverRequests.removeFriendDataInBackground(currentUser, selectedFriend, new GetUserCallback() {

                        @Override
                        public void done(User returnedUser) {

                        }

                        @Override
                        public void doneLocationTask(ArrayList<Marker> returnedLocations) {

                        }
                    });
                    Toast.makeText(FriendsListActivity.this, "Friend Removed", Toast.LENGTH_SHORT).show();
                    selectedFriend = "";
                }

                break;
        }
    }
}
