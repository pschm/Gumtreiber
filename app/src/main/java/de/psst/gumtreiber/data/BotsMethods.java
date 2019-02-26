package de.psst.gumtreiber.data;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class BotsMethods {

    private static DateFormat timeFormat = new SimpleDateFormat("HHmm");
    private static final String firebaseURL = "https://gumtreiber-1fb84.firebaseio.com";
    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public static void createBot(String botID, String name) {
        database.child("bots").child(botID).child("name").setValue(name);
    }

    public static void addRoutineToBot(String botID, long startTime, long endTime, Location location) {
        addRoutineToBot(botID,startTime,endTime,location.getLatitude(),location.getLongitude(), location.getAltitude());
    }

    public static void addRoutineToBot(String botID, long startTime, long endTime, double latitude, double longitude, double altitude) {

        database.child("bots").child(botID).child("routines").child(""+startTime).child("startTime").setValue(startTime);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("endTime").setValue(endTime);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("latitude").setValue(latitude);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("longitude").setValue(longitude);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("altitude").setValue(altitude);
    }

    public static ArrayList<Bot> getActiveBots(String authToken) {
        String jsonString = Firebase.getJSON(firebaseURL + "/bots" + ".json"  + "?auth=" + authToken);

        ArrayList<Bot> activeBots = new ArrayList<Bot>();

        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allBotIDs = reader.names();

            for (int i = 0; i < allBotIDs.length(); i++){

                //Get ID and name of the bot
                String botID = allBotIDs.getString(i);
                JSONObject botJSON = reader.getJSONObject(botID);
                String name = botJSON.getString("name");

                //Get current routine of the bot
                JSONArray routinesJSON = botJSON.getJSONObject("routines").names();


                for(int j = 0; j < routinesJSON.length(); j++){
                    //JSONObject myRoutine = routinesJSON.getJSONObject(j);
                    int routineID = routinesJSON.getInt(j);
                    JSONObject myRoutine = botJSON.getJSONObject("routines").getJSONObject(""+routineID);

                    int now = getCurrentTime();

                    int startTime = myRoutine.getInt("startTime");
                    int endTime = myRoutine.getInt("endTime");

                    if (startTime <= now && now <= endTime){
                        Bot myBot = new Bot(botID, name);
                        myBot.setLatitude(myRoutine.getDouble("latitude"));
                        myBot.setLongitude(myRoutine.getDouble("longitude"));
                        myBot.setAltitude(myRoutine.getDouble("altitude"));

                        Calendar end = Calendar.getInstance();
                        end.set(Calendar.HOUR_OF_DAY, endTime/100);
                        end.set(Calendar.MINUTE, endTime % 100);
                        end.set(Calendar.SECOND, 0);
                        myBot.setExpirationDate(end);

                        activeBots.add(myBot);

                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return activeBots;
    }

    private static int getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        int date = Integer.parseInt(timeFormat.format(cal.getTime()));
        return date;
    }


}
