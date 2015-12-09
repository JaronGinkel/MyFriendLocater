package com.example.brandon.myfriendlocater;

/**
 * Created by Brandon on 11/21/2015.
 */
public class User {
    String name, username, password, lat, lng;

    public User(String name, String username, String password)
    {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(String name, String username, String password, String lat, String lng)
    {
        this.name = name;
        this.username = username;
        this.password = password;
        this.lat = lat;
        this.lng = lng;
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.name = "";
    }
}
