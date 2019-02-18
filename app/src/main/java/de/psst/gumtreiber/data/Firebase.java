package de.psst.gumtreiber.data;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import de.psst.gumtreiber.location.Room;

//REST


public class Firebase {
    //Amount of minutes until the latest location data becomes invalid
    private static final int lifetimeMinutes = 5;
    //Firebase URL for GET requests
    private static final String firebaseURL = "https://gumtreiber-1fb84.firebaseio.com";

    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static DateFormat timeFormat = new SimpleDateFormat("HHmm");


    /**
     * Creates a new User in the database and initializes it's userdata.
     * If the user already exists, the name is updated.
     *
     * @param uid  The user id of the user
     * @param name name of the user
     */
    public static void createUser(String uid, String name) {
        database.child("users").child(uid).child("name").setValue(name);

        //Initialize properties of user
        setCurrentLocation(uid, 0, 0, 0);
        deactivateSchedule(uid);
    }

    public static void createUser(String uid, String name, Course course) {
        createUser(uid, name);
        if (course != null) setCourse(uid, course);
    }


    /**
     * Changes the name of the user in Firebase.
     *
     * @param uid  The user id of the user
     * @param name name of the user
     */
    public static void changeName(String uid, String name) {
        database.child("users").child(uid).child("name").setValue(name);
    }


    public static void setCourse(String uid, Course course) {
        database.child("users").child(uid).child("course").setValue(course.name());
    }


    /**
     * Updates the location of the user in firebase.
     *
     * @param user
     * @param location
     */
    public static void setCurrentLocation(FirebaseUser user, Location location) {
        if (user == null || location == null) return;
        setCurrentLocation(user.getUid(), location.getLatitude(), location.getLongitude(), location.getAltitude());
    }

    /**
     * Updates the location of the given user in firebase.
     *
     * @param uid
     * @param latitude
     * @param longitude
     * @param altitude
     */
    public static void setCurrentLocation(String uid, double latitude, double longitude, double altitude) {
        long expirationDate = generateExpirationDate();
        database.child("users").child(uid).child("latitude").setValue(latitude);
        database.child("users").child(uid).child("longitude").setValue(longitude);
        database.child("users").child(uid).child("altitude").setValue(altitude);
        database.child("users").child(uid).child("expirationDate").setValue(expirationDate);
    }

    /**
     * Adds an appointment to the users schedule in firebase. Keep in mind, that you have to make
     * sure, that the added appointments are consistent. That means, there shouldn't be any
     * overlapping appointments.
     * Also, you need to use activateSchedule() so that the schedule is actually used for a
     * virtual user on the map.
     *
     * @param uid
     * @param appointment
     */
    public static void addAppointmentToSchedule(String uid, Appointment appointment) {

        //int start = appointment.getFormatedStartTime();
        //int end = appointment.getFormatedEndTime();

        long start = appointment.getFormatedStartDate();
        long end = appointment.getFormatedEndDate();

        database.child("schedules").child(uid).child("" + start).child("startDate").setValue(start);
        database.child("schedules").child(uid).child("" + start).child("endDate").setValue(end);
        database.child("schedules").child(uid).child("" + start).child("room").setValue(appointment.getRoom().name());
    }

    /**
     * Deletes the given appointment from the user's schedule in firebase
     *
     * @param uid
     * @param appointment
     */
    public static void deleteAppointment(String uid, Appointment appointment) {
        long start = appointment.getFormatedStartDate();

        database.child("schedules").child(uid).child("" + start).removeValue();
    }

    /**
     * Requests all appointments of the user from Firebase and returns them as an ArrayList.
     *
     * @param uid
     * @param authToken TODO missing desc.
     * @return ArrayList
     */
    public static ArrayList<Appointment> getAppointments(String uid, String authToken) {
        ArrayList<Appointment> appointmentList = new ArrayList<>();

        String jsonString = getJSON(firebaseURL + "/schedules/" + uid + ".json" + "?auth=" + authToken);

        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allAppointments = reader.names();

            for (int i = 0; i < allAppointments.length(); i++) {
                String appointmentID = allAppointments.getString(i);
                JSONObject appointmentJSON = reader.getJSONObject(appointmentID);

                long startDate = appointmentJSON.getLong("startDate");
                long endDate = appointmentJSON.getLong("endDate");

                String roomString = appointmentJSON.getString("room");
                Room room = Room.valueOf(roomString);

                Appointment myAppointment = new Appointment(startDate, endDate, room);

                appointmentList.add(myAppointment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return appointmentList;
    }


    /**
     * Generates an expiration date for the location data in the form of yyyyMMddHHmmss.
     * The lifetime of the expiration date is determined by the lifetimeMinutes constant.
     *
     * @return expiration date in the form of yyyyMMddHHmmss
     */
    private static long generateExpirationDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, lifetimeMinutes);
        long date = Long.parseLong(dateFormat.format(cal.getTime()));
        return date;
    }

    /**
     * Generates the current date in the form of yyyyMMddHHmmss.
     *
     * @return date in the form of yyyyMMddHHmmss
     */
    private static long generateCurrentDate() {
        Calendar cal = Calendar.getInstance();
        long date = Long.parseLong(dateFormat.format(cal.getTime()));
        return date;
    }

    //TODO Javadoc
    private static int generateCurrentTime() {
        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(timeFormat.format(cal.getTime()));
        return time;
    }

    /**
     * Activates the schedule of the user. By doing so, the user will appear as a bot on the map,
     * controlled by their schedule
     *
     * @param uid
     */
    public static void activateSchedule(String uid) {
        database.child("users").child(uid).child("usingSchedule").setValue(true);
        //database.child("users").child(uid).child("expirationDate").setValue(0);
    }

    /**
     * By deactivating the schedule, only the current location data is considered for the user.
     *
     * @param uid
     */
    public static void deactivateSchedule(String uid) {
        database.child("users").child(uid).child("usingSchedule").setValue(false);
    }

    public static void updateUserList(String authToken, HashMap<String, User> userList) {
        String jsonString = getJSON(firebaseURL+ "/users.json" + "?auth=" + authToken);
        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allUIDs = reader.names();
            //String s = user1.getString(2);
            //Log.v("mim",""+user1.length());

            for(int i = 0; i < allUIDs.length(); i++) {
                String userUid = allUIDs.getString(i);
                JSONObject userJSON = reader.getJSONObject(userUid);


                User myUser; //Check if user has a entry in the userList
                if(userList.containsKey(userUid)) {
                    myUser = userList.get(userUid); //true, only UPDATE its values
                } else {
                    myUser = new User(userUid, userJSON.getString("name")); //false, create the user
                    userList.put(userUid, myUser); //and add him/her to the list
                }


                myUser.altitude = userJSON.getDouble("altitude");
                myUser.latitude = userJSON.getDouble("latitude");
                myUser.longitude = userJSON.getDouble("longitude");
                myUser.usingSchedule = userJSON.getBoolean("usingSchedule");

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(userJSON.getString("expirationDate")));
                myUser.expirationDate = cal;

                if (userJSON.has("course")){
                    String courseString = userJSON.getString("course");
                    myUser.setCourse(Course.valueOf(courseString));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void newUpdateUserList(String authToken, HashMap<String, User> userList) {
        ArrayList<User> allUser = getAllUsers(authToken);

        for (User each : allUser) {
            User myUser;
            if (userList.containsKey(each.uid)) {
                myUser = userList.get(each.uid);

                //Update Data, that may has changed.
                myUser.usingSchedule = each.usingSchedule;
                myUser.name = each.name;
                myUser.setCourse(each.getCourse());

                if (myUser.usingSchedule) {
                    //User uses his schedule:
                    Appointment currentAppointment = getCurrentAppointment(myUser.uid, authToken);

                    if (currentAppointment != null) {
                        if (myUser.getMarker() != null) myUser.getMarker().setVisibility(true);
                        myUser.altitude = currentAppointment.getRoom().getAltitude();
                        myUser.latitude = currentAppointment.getRoom().getLatitude();
                        myUser.longitude = currentAppointment.getRoom().getLongitude();
                        myUser.expirationDate = currentAppointment.getEndDate();

                    } else {
                        //User has no current Appointment
                        if (myUser.getMarker() != null) myUser.getMarker().setVisibility(false);
                    }


                } else {
                    //User uses location data:

                    if (myUser.isExpired()) {
                        //Make User invisible if he is expired:
                        if (myUser.getMarker() != null) myUser.getMarker().setVisibility(false);
                    } else {
                        //Make User visible if he is not expired and update user data:
                        if (myUser.getMarker() != null) myUser.getMarker().setVisibility(true);

                        myUser.altitude = each.altitude;
                        myUser.latitude = each.latitude;
                        myUser.longitude = each.longitude;
                        myUser.expirationDate = each.expirationDate;

                    }
                }


            } else {
                if(each.usingSchedule) {
                    //User uses his schedule:
                    Appointment currentAppointment = getCurrentAppointment(each.uid, authToken);

                    if(currentAppointment != null) {

                        each.altitude = currentAppointment.getRoom().getAltitude();
                        each.latitude = currentAppointment.getRoom().getLatitude();
                        each.longitude = currentAppointment.getRoom().getLongitude();
                        each.expirationDate = currentAppointment.getEndDate();

                        userList.put(each.uid, each);

                    } else {
                        //User has no current Appointment
                        //User is ignored
                    }


                } else {
                    //User uses location data:

                    if (each.isExpired()){
                        //Make User invisible if he is expired:
                        //User is ignored
                    } else {
                        //Make User visible if he is not expired and update user data:
                        userList.put(each.uid, each);
                    }
                }
            }

        }
    }

    //TODO JavaDoc
    private static Appointment getCurrentAppointment(String uid, String authToken) {

        ArrayList<Appointment> appointments = getAppointments(uid, authToken);
        Appointment currentAppointment = null;
        long currentDate = generateCurrentDate();
        for (Appointment each : appointments) {
            if (each.getFormatedStartDate() <= currentDate &&
                    each.getFormatedEndDate() >= currentDate) {
                currentAppointment = each;
                break;
            }

        }

        //null, if there is no current appointment
        return currentAppointment;
    }


    /**
     * Requests all useres from firebase and returns them with their location data in an
     * ArrayList.
     *
     * @param authToken
     * @return all useres with their location data
     */
    public static ArrayList<User> getAllUsers(String authToken) {
        ArrayList<User> userList = new ArrayList<>();

        String jsonString = getJSON(firebaseURL + "/users.json" + "?auth=" + authToken);

        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allUIDs = reader.names();

            for (int i = 0; i < allUIDs.length(); i++) {
                String userUid = allUIDs.getString(i);
                JSONObject userJSON = reader.getJSONObject(userUid);

                User myUser = new User(userUid, userJSON.getString("name"));
                myUser.altitude = userJSON.getDouble("altitude");
                myUser.latitude = userJSON.getDouble("latitude");
                myUser.longitude = userJSON.getDouble("longitude");
                myUser.usingSchedule = userJSON.getBoolean("usingSchedule");

                //Wenn Nutzer einen Studiengang hat, dann setze ihn
                if (userJSON.has("course")){
                    String courseString = userJSON.getString("course");
                    myUser.setCourse(Course.valueOf(courseString));
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse("" + userJSON.getLong("expirationDate")));
                myUser.expirationDate = cal;

                userList.add(myUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    //TODO Nicht mehr gebraucht

    /**
     * Requests all useres from firebase with valid locations and returns them with their
     * location data in an ArrayList. Users with expired location data will be ignored.
     *
     * @param authToken
     * @return all active useres with their location data
     */
    public static ArrayList<User> getAllActiveUsers(String authToken) {
        ArrayList<User> userList = new ArrayList<>();

        final long date = generateCurrentDate();
        String jsonString = getJSON(firebaseURL + "/users.json" + "?orderBy=\"expirationDate\"&startAt=" + date + "&auth=" + authToken);

        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allUIDs = reader.names();

            for (int i = 0; i < allUIDs.length(); i++) {
                String userUid = allUIDs.getString(i);
                JSONObject userJSON = reader.getJSONObject(userUid);

                User myUser = new User(userUid, userJSON.getString("name"));
                myUser.altitude = userJSON.getDouble("altitude");
                myUser.latitude = userJSON.getDouble("latitude");
                myUser.longitude = userJSON.getDouble("longitude");
                myUser.usingSchedule = userJSON.getBoolean("usingSchedule");

                if (userJSON.has("course")){
                    String courseString = userJSON.getString("course");
                    myUser.setCourse(Course.valueOf(courseString));
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse("" + userJSON.getLong("expirationDate")));
                myUser.expirationDate = cal;

                userList.add(myUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    //TODO nicht mehr gebracuht

    /**
     * Requests all users with an activated schedule, looks for their current appointment and puts
     * the user and the location of the current appointment in an ArrayList.
     *
     * @param authToken
     * @return Arraylist with the appointments of all scheduled users.
     */
    public static ArrayList<User> getAllScheduledUsers(String authToken) {
        ArrayList<User> userList = new ArrayList<>();

        String jsonString = getJSON(firebaseURL + "/users.json" + "?orderBy=\"usingSchedule\"&startAt=" + true + "&auth=" + authToken);

        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allUIDs = reader.names();

            for (int i = 0; i < allUIDs.length(); i++) {
                String userUid = allUIDs.getString(i);
                JSONObject userJSON = reader.getJSONObject(userUid);
                User myUser = new User(userUid, userJSON.getString("name"));

                //Determine the current Appointment
                ArrayList<Appointment> appointments = getAppointments(userUid, authToken);
                Appointment currentAppointment = null;
                long currentDate = generateCurrentDate();
                for (Appointment each : appointments) {
                    if (each.getFormatedStartDate() <= currentDate &&
                            each.getFormatedEndDate() >= currentDate) {
                        currentAppointment = each;
                        break;
                    }

                }

                //Add user with current Appointment to ArrayList
                if (currentAppointment != null) {
                    myUser.altitude = currentAppointment.getRoom().getAltitude();
                    myUser.latitude = currentAppointment.getRoom().getLatitude();
                    myUser.longitude = currentAppointment.getRoom().getLongitude();
                    myUser.usingSchedule = true;

                    if (userJSON.has("course")){
                        String courseString = userJSON.getString("course");
                        myUser.setCourse(Course.valueOf(courseString));
                    }

                    myUser.expirationDate = currentAppointment.getEndDate();

                    userList.add(myUser);
                } else {
                    continue;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * Adds the UID of your friend to your friendlist in firebase.
     * @param myUID UID of the friendlist's owner
     * @param friendsUID UID of the user to be added to the friendlist
     */
    public static void addUserToFriendlist(String myUID, String friendsUID) {
        database.child("friendlists").child(myUID).child(friendsUID).setValue(true);
    }

    /**
     * Deletes the UID of your friend from your friendlist in firebase.
     * @param myUID UID of the friendlist's owner
     * @param friendsUID UID of the user to be deleted from the friendlist
     */
    public static void deleteUserFromFriendlist(String myUID, String friendsUID) {
        database.child("friendlists").child(myUID).child(friendsUID).removeValue();

    }

    /**
     * Returns a String Array with all UIDs of your friends.
     * @param uid UID of the friendlist's owner
     * @param authToken token of the friendlist's owner
     * @return
     */
    public static String[] getFriendlist(String uid, String authToken){
        String jsonString = getJSON(firebaseURL + "/friendlists/" + uid + ".json"  + "?auth=" + authToken);
        String[] friendlist = {""};


        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray allUIDs = reader.names();
            friendlist = new String[allUIDs.length()];

            for (int i = 0; i < friendlist.length; i++){
                friendlist[i] = allUIDs.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return friendlist;

    }



    /**
     * Makes a GET request to Firebase and returns a JSON
     *
     * @return JSON-String
     */
    private static String getJSON(final String urlGet) {
        final Semaphore sem = new Semaphore(0);
        final StringBuilder json = new StringBuilder();


        //Android needs a background thread in order to run network operations
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(urlGet);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed 'getUserJSON': HTTP error code: " + conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String output;
                    if ((output = br.readLine()) != null) {
                        json.append(output);

                        try {
                            sem.release();
                        } catch (Exception e) {
                        }
                        ;
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
        try {
            sem.acquire();
        } catch (Exception e) {
        }
        ;
        return json.toString();
    }


}