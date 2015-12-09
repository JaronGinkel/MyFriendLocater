package com.example.brandon.myfriendlocater;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

/**
 * Created by Brandon on 11/21/2015.
 */
interface GetUserCallback {
    public abstract void done(User returnedUser);
    public abstract void doneLocationTask(ArrayList<Marker> returnedLocations);
}
