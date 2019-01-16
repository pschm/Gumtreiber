package de.psst.gumtreiber.data;

import android.location.Location;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

//REST


public class Firebase {
    //Amount of minutes until the latest location data becomes invalid
    private static final int lifetimeMinutes = 5;
    private static final String firebaseURL = "https://gumtreiber-1fb84.firebaseio.com/users.json";

    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private static DateFormat dateFormat = new SimpleDateFormat("/yyyy/MM/dd HH:mm:ss");


    public static void createUser(String uid, String name) {
        database.child("users").child(uid).child("name").setValue(name);

        //Initialize properties of user
        setCurrentLocation(uid, 0,0,0);
        deactivateSchedule(uid);
    }

    public static void setCurrentLocation(FirebaseUser user, Location location) {
        if(user == null || location == null) return;
        setCurrentLocation(user.getUid(), location.getLatitude(), location.getLongitude(), location.getAltitude());
    }

    public static void setCurrentLocation(String uid, double latitude, double longitude, double altitude) {
        String expirationDate = generateExpirationDate();
        database.child("users").child(uid).child("latitude").setValue(latitude);
        database.child("users").child(uid).child("longitude").setValue(longitude);
        database.child("users").child(uid).child("altitude").setValue(altitude);
        database.child("users").child(uid).child("expirationDate").setValue(expirationDate);
    }

    private static String generateExpirationDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, lifetimeMinutes);
        String s = dateFormat.format(cal.getTime());

        return s;
    }

    public static void activateSchedule(String uid) {
        database.child("users").child(uid).child("usingSchedule").setValue(true);
    }

    public static synchronized void deactivateSchedule(String uid) {
        database.child("users").child(uid).child("usingSchedule").setValue(false);
    }






     public static ArrayList<User> getAllUsers(String authToken) {
        ArrayList<User> userList= new ArrayList<>();
        String jsonString = getUserJSON(authToken);

        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allUIDs = reader.names();
            //String s = user1.getString(2);
            //Log.v("mim",""+user1.length());

            for(int i = 0; i < allUIDs.length(); i++) {
                String userUid = allUIDs.getString(i);
                JSONObject userJSON = reader.getJSONObject(userUid);

                User myUser = new User(userUid, userJSON.getString("name"));
                myUser.altitude = userJSON.getDouble("altitude");
                myUser.latitude = userJSON.getDouble("latitude");
                myUser.longitude = userJSON.getDouble("longitude");
                myUser.usingSchedule = userJSON.getBoolean("usingSchedule");

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(userJSON.getString("expirationDate")));
                myUser.expirationDate = cal;

                userList.add(myUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }


    /**
     * Makes a GET request to Firebase for the data of all users
     * @return JSON-String with all userdata
     */
    public static String getUserJSON(final String authToken) {
        final Semaphore sem = new Semaphore(0);
        final StringBuilder json = new StringBuilder();

        //Android needs a background thread in order to run network operations
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(firebaseURL + "?auth=" + authToken); //needed because of new security guidelines
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    if(conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed 'getUserJSON': HTTP error code: " + conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String output;
                    //Log.v("mim","Output from Server .... \n");
                    if ((output = br.readLine()) != null) {
                        //Log.v("mim",output);
                        json.append(output);

                        try{sem.release();}catch(Exception e){};
                    }

                    conn.disconnect();

                } catch (MalformedURLException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
        });

        //Waiting for JSON
        try{sem.acquire();}catch(Exception e){};
        return json.toString();
    }


}