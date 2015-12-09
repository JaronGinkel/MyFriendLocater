package com.example.brandon.myfriendlocater;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Brandon on 11/21/2015.
 */
public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://playchesswithbrandon.net/";

    public ServerRequests(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback){
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback).execute();
    }

    public void storeEventDataInBackground(User user, String title, String date, String time, String lat, String lng, GetUserCallback userCallback){
        progressDialog.show();
        new StoreEventDataAsyncTask(user, title, date, time, lat, lng, userCallback).execute();
    }

    public void storeLocationDataInBackground(User user, GetUserCallback userCallback){
        progressDialog.show();
        new StoreLocationDataAsyncTask(user, userCallback).execute();
    }

    public void storeFriendDataInBackground(User user, String friendName, GetUserCallback userCallback){
        progressDialog.show();
        new StoreFriendDataAsyncTask(user,friendName, userCallback).execute();
    }

    public void removeFriendDataInBackground(User user, String friendName, GetUserCallback userCallback){
        progressDialog.show();
        new RemoveFriendDataAsyncTask(user,friendName, userCallback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback callBack){
        progressDialog.show();
        new fetchUserDataAsyncTask(user, callBack).execute();
    }

    public void fetchFriendLocationDataInBackground(User user, GetUserCallback callBack){
        progressDialog.show();
        new fetchFriendLocationDataAsyncTask(user, callBack).execute();
    }

    public void fetchUserFriendListDataInBackground(User user, GetFriendListCallback callBack){
        progressDialog.show();
        new fetchUserFriendListDataAsyncTask(user, callBack).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback){
            this.user = user;
            this.userCallback = userCallback;
        }
        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name", user.name));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallback;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }
        @Override
        protected User doInBackground(Void... params){
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name", user.name));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");
            User returnedUser = null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                if(jObject.length()==0){
                    returnedUser = null;
                }else{
                    String name = jObject.getString("name");

                    returnedUser = new User(name, user.username, user.password);

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return returnedUser;
        }
        @Override
        protected void onPostExecute(User returnedUser){
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }


    public class fetchUserFriendListDataAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
        User user;
        GetFriendListCallback friendListCallback;

        public fetchUserFriendListDataAsyncTask(User user, GetFriendListCallback friendListCallback) {
            this.user = user;
            this.friendListCallback = friendListCallback;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "GetFriendList.php");
            ArrayList<String> returnedFriendList = new ArrayList<>();
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray jArray = new JSONArray(result);

                if(jArray.length()==0){
                    returnedFriendList = null;
                }else{
                    for (int i = 0; i < jArray.length(); i++ ) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        returnedFriendList.add(jObject.getString("username"));
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return returnedFriendList;
        }
        @Override
        protected void onPostExecute(ArrayList<String> returnedFriendList){
            progressDialog.dismiss();
            friendListCallback.doneFriendListTask(returnedFriendList);
            super.onPostExecute(returnedFriendList);
        }
    }



    public class fetchFriendLocationDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Marker>> {

        User user;
        GetUserCallback userCallback;

        public fetchFriendLocationDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }
        @Override
        protected ArrayList<Marker> doInBackground(Void... params){
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "GetFriendLocation.php");
            ArrayList<Marker> returnedLocations = new ArrayList<>();
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray jArray = new JSONArray(result);

                if(jArray.length()==0){
                    returnedLocations = null;
                }else{
                    for (int i = 0; i < jArray.length(); i++ ) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        returnedLocations.add(new Marker(jObject.getString("username"), jObject.getString("lat"), jObject.getString("lng")));
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return returnedLocations;
        }
        @Override
        protected void onPostExecute(ArrayList<Marker> returnedLocations){
            progressDialog.dismiss();
            userCallback.doneLocationTask(returnedLocations);
            super.onPostExecute(returnedLocations);
        }
    }
    public class StoreFriendDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;
        String friendName;
        public StoreFriendDataAsyncTask(User user, String friendName, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
            this.friendName = friendName;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("friendname", friendName));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "UpdateFriendName.php");
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }
    public class StoreEventDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;
        String title, date, time, lat, lng;
        public StoreEventDataAsyncTask(User user, String title, String date, String time, String lat, String lng, GetUserCallback userCallback) {
            this.user = user;
            this.title = title;
            this.date = date;
            this.time = time;
            this.lat = lat;
            this.lng = lng;
            this.userCallback = userCallback;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("lat", lat));
            dataToSend.add(new BasicNameValuePair("lng", lng));
            dataToSend.add(new BasicNameValuePair("title", title));
            dataToSend.add(new BasicNameValuePair("date", date));
            dataToSend.add(new BasicNameValuePair("time", time));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "InsertEvent.php");
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }
    public class StoreLocationDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreLocationDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }
        @Override
        protected Void doInBackground(Void... params){
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("lat", user.lat));
            dataToSend.add(new BasicNameValuePair("lng", user.lng));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "UpdateUserLocation.php");
            User returnedUser = null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class RemoveFriendDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;
        String friendName;
        public RemoveFriendDataAsyncTask(User user, String friendName, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
            this.friendName = friendName;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("friendname", friendName));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "DeleteFriendName.php");
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

}
