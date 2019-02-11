package de.psst.gumtreiber.data;

//TODO Make sure that the start time is before the end time

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.psst.gumtreiber.location.Room;

public class Appointment {

    private Room room;

    //Appointment should use a complete Date
    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private Calendar startDate;
    private Calendar endDate;

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

    public String toString() {
        return getFormatedStartDate()+ " - " + getFormatedEndDate() + " at " + room.name();
    }

}
