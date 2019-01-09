package de.psst.gumtreiber.data;

import android.os.AsyncTask;

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
    private static final String firebaseURL = "https://androidistdoof-20afd.firebaseio.com/user.json";

    private static final DatabaseReference datebase = FirebaseDatabase.getInstance().getReference();
    private static DateFormat dateFormat = new SimpleDateFormat("/yyyy/MM/dd HH:mm:ss");


    public static void createUser(String imei, String name) {
        datebase.child("user").child(imei).child("name").setValue(name);

        //Initialize properties of user
        setCurrentLocation(imei, 0,0,0);
        deactivateSchedule(imei);
    }

    public static void setCurrentLocation(String imei, double latitude, double longitude, double altitude) {
        String expirationDate = generateExpirationDate();
        datebase.child("user").child(imei).child("latitude").setValue(latitude);
        datebase.child("user").child(imei).child("longitude").setValue(longitude);
        datebase.child("user").child(imei).child("altitude").setValue(altitude);
        datebase.child("user").child(imei).child("expirationDate").setValue(expirationDate);
    }

    private static String generateExpirationDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, lifetimeMinutes);
        String s = dateFormat.format(cal.getTime());

        return s;
    }

    public static void activateSchedule(String imei) {
        datebase.child("user").child(imei).child("usingSchedule").setValue(true);
    }

    public static synchronized void deactivateSchedule(String imei) {
        datebase.child("user").child(imei).child("usingSchedule").setValue(false);
    }






     public static ArrayList<User> getAllUsers() {
        ArrayList<User> userList= new ArrayList<>();
        String jsonString = getUserJSON();

        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allIMEI = reader.names();
            //String s = user1.getString(2);
            //Log.v("mim",""+user1.length());

            for(int i = 0; i < allIMEI.length(); i++) {
                String userIMEI = allIMEI.getString(i);
                JSONObject userJSON = reader.getJSONObject(userIMEI);

                User myUser = new User(userIMEI, userJSON.getString("name"));
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

        }

        return userList;
    }


    /**
     * Makes a GET request to Firebase for the data of all users
     * @return JSON-String with all userdata
     */
    public static String getUserJSON() {
        final Semaphore sem = new Semaphore(0);
        final StringBuilder json = new StringBuilder();

        //Android needs a background thread in order to run network operations
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(firebaseURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    /*if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }*/

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

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