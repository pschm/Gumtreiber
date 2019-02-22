package de.psst.gumtreiber.data;

//TODO Make sure that the start time is before the end time

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.psst.gumtreiber.location.Room;

public class Appointment {

    //Appointment should use a complete Date
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdfReadableDate = new SimpleDateFormat("dd.MM.yyyy");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdfReadableTime = new SimpleDateFormat("HH:mm");

    private Calendar startDate;
    private Calendar endDate;
    private Room room;

    public Appointment(Calendar startDate, Calendar endDate, Room room) {
        this.startDate = startDate;
        this.endDate = endDate;

        this.room = room;
    }

    public Appointment(long formatedStartDate, long formatedEndDate, Room room) {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
            start.setTime(dateFormat.parse(""+formatedStartDate ));
            end.setTime(dateFormat.parse(""+formatedEndDate ));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.startDate = start;
        this.endDate = end;
        this.room = room;
    }

    //TODO Christopher fragen warum nicht getFormatetDate(Date date) {...} ???
    /**
     * Returns the start date of the appointment in the form of yyyyMMddHHmmss as a long value
     * @return start date in the form of a long value
     */
    public long getFormatedStartDate() {
        long date = Long.parseLong( dateFormat.format(startDate.getTime()) );
        return date;
    }

    /**
     * Returns the end date of the appointment in the form of yyyyMMddHHmmss as a long value
     * @return end date in the form of a long value
     */
    public long getFormatedEndDate() {
        long date = Long.parseLong( dateFormat.format(endDate.getTime()) );
        Log.v("DATE", date + "");
        return date;
    }

    public void setFormatedStartDate(long formatedStartDate) {
        Calendar start = Calendar.getInstance();
        try {
            start.setTime(dateFormat.parse(""+formatedStartDate ));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.startDate = start;
    }

    public void setFormatedEndDate(long formatedEndDate) {
        Calendar end = Calendar.getInstance();
        try {
            end.setTime(dateFormat.parse(""+formatedEndDate ));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.startDate = end;
    }

    public String toString() {
        return getFormatedStartDate() + " - " + getFormatedEndDate() + " at " + room.name();
    }


    //Methods to show the date & time in the Ui

    /**
     * @return The start date of this appointment in a common calender date format (dd.MM.yyyy).
     */
    public String getReadableStartDate() {
        return sdfReadableDate.format(startDate.getTime());
    }

    /**
     * @return The end date of this appointment in a common calender date format (dd.MM.yyyy).
     */
    public String getReadableEndDate() {
        return sdfReadableDate.format(endDate.getTime());
    }

    /**
     * @return The start time of this appointment in a common time format (HH:mm).
     */
    public String getReadableStartTime() {
        return sdfReadableTime.format(startDate.getTime());
    }

    /**
     * @return The end time of this appointment in a common time format (HH:mm).
     */
    public String getReadableEndTime() {
        return sdfReadableTime.format(endDate.getTime());
    }


    /**
     * Formats a Date given as long "YYYYMMDDHHMMSS" to a nicely readable String
     *
     * @param unreadableDate a formatet date as used in the Database
     * @return a Sting with the format "DD.MM.YYYY"
     */
    public String getReadableDate(long unreadableDate) {

        String stringDate = Long.toString(unreadableDate);

        String year = stringDate.substring(0, 4);
        String month = stringDate.substring(4, 6);
        String day = stringDate.substring(6, 8);

        return day + "." + month + "." + year;
    }

    /**
     * * Formats a date/time given as long "YYYYMMDDHHMMSS" to a nicely readable String
     *
     * @param unreadableDate a formatet date/time as used in the Database
     * @return a String with the Format "HH:MM"
     */
    public String getReadableTime(long unreadableDate) {

        String stringDate = Long.toString(unreadableDate);

        String hour = stringDate.substring(8, 10);
        String minute = stringDate.substring(10, 12);


        return hour + ":" + minute;
    }

    //TODO SVEN fragen wie die Annotation richtig geht !

    /**
     * Formats a Date given as long "YYYYMMDDHHMMSS" to a nicely readable String
     * by combining the use of #getReadableTime and #getReadableDate
     *
     * @param unreadableDate a formatet date as used in the Database
     * @return a String with the Format "DD.MM.YYYY - HH:MM"
     */
    public String getStringDate(long unreadableDate) {
        return getReadableDate(unreadableDate) + " - " + getReadableTime(unreadableDate);
    }


    //Various getters and setters

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }


}