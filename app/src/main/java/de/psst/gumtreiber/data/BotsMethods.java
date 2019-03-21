package de.psst.gumtreiber.data;

import android.annotation.SuppressLint;
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

    @SuppressLint("SimpleDateFormat")
    private static DateFormat timeFormat = new SimpleDateFormat("HHmm");
    private static final String firebaseURL = "https://gumtreiber-1fb84.firebaseio.com";
    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    /**
     * Creates a bot with the given name and botID. It's your responsibility to check if a id is
     * already used, hen you use this method. If you input a used id, you will overwrite the name
     * of the bot.
     *
     * @param botID UID of the bot
     * @param name name of the bot. Will be displayed on the map
     */
    public static void createBot(String botID, String name) {
        database.child("bots").child(botID).child("name").setValue(name);
    }

    /**
     * Adds a routine to the bot
     *
     * @param botID id of the bot
     * @param startTime start time of the routine in the form of HHmm
     * @param endTime end time of the routine in the form of HHmm
     * @param location location of the routine
     */
    public static void addRoutineToBot(String botID, int startTime, int endTime, Location location) {
        addRoutineToBot(botID,startTime,endTime,location.getLatitude(),location.getLongitude(), location.getAltitude());
    }

    /**
     * Adds a routine to the bot
     *
     * @param botID id of the bot
     * @param startTime start time of the routine in the form of HHmm
     * @param endTime end time of the routine in the form of HHmm
     * @param latitude latitude of the routine
     * @param longitude longitude of the routine
     * @param altitude altitude of the routine
     */
    public static void addRoutineToBot(String botID, int startTime, int endTime, double latitude, double longitude, double altitude) {

        database.child("bots").child(botID).child("routines").child(""+startTime).child("startTime").setValue(startTime);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("endTime").setValue(endTime);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("latitude").setValue(latitude);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("longitude").setValue(longitude);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("altitude").setValue(altitude);
    }

    /**
     * Builds an ArrayList with all active bots and their current location data.
     *
     * @param authToken The token which authenticates the user
     * @return ArrayList with all active bots and their current location data
     */
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

    /**
     * Generates the current date in the form of HHmm.
     *
     * @return date in the form of HHmm
     */
    private static int getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        int date = Integer.parseInt(timeFormat.format(cal.getTime()));
        return date;
    }


}
