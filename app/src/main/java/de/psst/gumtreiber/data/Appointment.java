package de.psst.gumtreiber.data;

//TODO Make sure that the start time is before the end time

import de.psst.gumtreiber.location.Room;

public class Appointment {
    private int startHour;
    private int startMinute;

    private int endHour;
    private int endMinute;

    private Room room;

    public Appointment(int startHour, int startMinute, int endHour, int endMinute, Room room) {
        setStartHour(startHour);
        setStartMinute(startMinute);

        setEndHour(endHour);
        setEndMinute(endMinute);

        this.room = room;
    }


    /**
     *
     * @param formatedStartTime Time in the form of HHmm (i.e. 16:35 as 1635)
     * @param formatedEndTime Time in the form of HHmm (i.e. 16:35 as 1635)
     * @param room
     */
    public Appointment(int formatedStartTime, int formatedEndTime, Room room){
        setFormatedStartTime(formatedStartTime);
        setFormatedEndTime(formatedEndTime);

        this.room = room;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour % 24;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute % 60;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour % 24;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute % 60;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String toString() {
        return startHour+":"+startMinute + " - " + endHour+":"+endMinute + " at " + room.name();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Methods for Firebase use:

    /**
     * Returns the starting time as an integer.
     * 16:35 will be returned as 1635.
     * @return formated start time
     */
    public int getFormatedStartTime() {
        int formatedTime = startHour * 100 + startMinute;
        return formatedTime;
    }

    /**
     * Returns the ending time as an integer.
     * 16:35 will be returned as 1635.
     * @return formated end time
     */
    public int getFormatedEndTime() {
        int formatedTime = endHour * 100 + endMinute;
        return formatedTime;
    }

    /**
     * Takes an integer with a time in the form of HHmm and saves it as the starting time of
     * the appointment. The integer should represent 16:35 as 1635.
     * @param formatedStartTime A time as an integer in the form of HHmm
     */
    public void setFormatedStartTime(int formatedStartTime){
        if (formatedStartTime == 0) {
            setStartHour(0);
            setStartMinute(0);
            return;
        }

        int startHour = formatedStartTime / 100;
        int startMinute = formatedStartTime - (startHour * 100);

        setStartHour(startHour);
        setStartMinute(startMinute);
    }

    /**
     * Takes an integer with a time in the form of HHmm and saves it as the ending time of
     * the appointment. The integer should represent 16:35 as 1635.
     * @param formatedEndTime A time as an integer in the form of HHmm
     */
    public void setFormatedEndTime(int formatedEndTime){
        if (formatedEndTime == 0) {
            setEndHour(0);
            setEndMinute(0);
            return;
        }

        int endHour = formatedEndTime / 100;
        int endMinute = formatedEndTime - (endHour * 100);

        setEndHour(endHour);
        setEndMinute(endMinute);
    }
}
