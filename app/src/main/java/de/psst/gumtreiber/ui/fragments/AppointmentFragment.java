package de.psst.gumtreiber.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.location.Room;


public class AppointmentFragment extends Fragment {


    //TODO Kontrolle auf Zeitpunkte -> Zeitreisen sind nicht möglich
    //TODO Zeugs nach ViewModel auslagern

    private String uid;
    private GregorianCalendar c = new GregorianCalendar();
    private Activity activity;
    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_appointment, container, false);
        activity = getActivity();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return fragmentView;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        Button btnSubmitAppointment = activity.findViewById(R.id.btn_submit_appointment);
        btnSubmitAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Termin Speichern

                activity.onBackPressed();
            }
        });
    }

    //TODO In mehrere Metohden unterteilen
    /**
     * Initiates the TextViews with Date & Time Pickers in a barbaric kind of way
     */
    public void initViews() {

        //Spinner for the Rooms
        spinner = activity.findViewById(R.id.spinn_room);
        spinner.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, Room.values()));




        //Termin Anfang
        TextView tvStartDate = activity.findViewById(R.id.tv_start_date);
        tvStartDate.setText(getCurrentDate());
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                TextView tvStartDate = activity.findViewById(R.id.tv_start_date);
                                tvStartDate.setText(getReadableDate(day, month, year));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        TextView tvStartTime = activity.findViewById(R.id.tv_start_time);
        tvStartTime.setText(getCurrentTime());
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hourOfDay = c.get(GregorianCalendar.HOUR_OF_DAY);
                int minute = c.get(GregorianCalendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                TextView tvStartTime = activity.findViewById(R.id.tv_start_time);
                                tvStartTime.setText(getReadableTime(hourOfDay, minute));
                            }
                        }, hourOfDay, minute, true);
                timePickerDialog.show();
            }
        });


        //Termin Ende
        TextView tvEndDate = activity.findViewById(R.id.tv_end_date);
        tvEndDate.setText(getCurrentDate());
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                TextView tvStartDate = activity.findViewById(R.id.tv_end_date);
                                tvStartDate.setText(getReadableDate(day, month, year));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        TextView tvEndTime = activity.findViewById(R.id.tv_end_time);
        tvEndTime.setText(getCurrentTime());
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hourOfDay = c.get(GregorianCalendar.HOUR_OF_DAY);
                int minute = c.get(GregorianCalendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                TextView tvEndTime = activity.findViewById(R.id.tv_end_time);
                                tvEndTime.setText(getReadableTime(hourOfDay, minute));
                            }
                        }, hourOfDay, minute, true);
                timePickerDialog.show();
            }
        });

        Button btnSave = activity.findViewById(R.id.btn_submit_appointment);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointment();
            }
        });
    }

    /**
     * Getting the current date as a String
     *
     * @return dd.mm.yyyy String of the Current Date
     */
    private String getCurrentDate() {

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return getReadableDate(day, month, year);

    }

    /**
     * Formats a date given as integers (day,month,year) to a String with the dd.mm.yyyy format
     *
     * @param day
     * @param month
     * @param year
     * @return Formatted date String
     */
    private String getReadableDate(int day, int month, int year) {

        month += 1;
        String sDay = Integer.toString(day);
        String sMonth = Integer.toString((month));
        String sYear = Integer.toString(year);

        if (day <= 9) sDay = "0" + day;
        if (month <= 9) sMonth = "0" + month;

        return sDay + "." + sMonth + "." + sYear;
    }

    /**
     * Getting the current time as a String
     *
     * @return hh:mm String of the current time
     */
    private String getCurrentTime() {

        int hour = c.get(GregorianCalendar.HOUR);
        int minute = c.get(GregorianCalendar.MINUTE);

        return getReadableTime(hour, minute);

    }

    /**
     * Formats a time given as integers (hour,minutes) to a String with the hh:mm format
     *
     * @param hour
     * @param minute
     * @return Formatted time String
     */
    private String getReadableTime(int hour, int minute) {

        String sHour = Integer.toString(hour);
        String sMinute = Integer.toString(minute);

        if (Integer.parseInt(sHour) <= 9) sHour = "0" + sHour;
        if (Integer.parseInt(sMinute) <= 9) sMinute = "0" + sMinute;

        return sHour + ":" + sMinute;
    }


    private void saveAppointment() {

        //Room
        Room room = (Room) spinner.getSelectedItem();

        //Start Date
        TextView tvStartDate = activity.findViewById(R.id.tv_start_date);
        TextView tvStartTime = activity.findViewById(R.id.tv_start_time);

        String startDate = tvStartDate.getText().toString();
        String startTime = tvStartTime.getText().toString();

        //End Date
        TextView tvEndDate = activity.findViewById(R.id.tv_end_date);
        TextView tvEndTime = activity.findViewById(R.id.tv_end_time);

        String endDate = tvEndDate.getText().toString();
        String endTime = tvEndTime.getText().toString();

        //TODO in ViewModel auslagern
        Appointment appointment = new Appointment(formatDate(startDate, startTime), formatDate(endDate, endTime), room);
        Firebase.addAppointmentToSchedule(uid, appointment);

    }

    /**
     * Formats a date given as "DD.MM.YYYY","HH:MM" to a long with the Format "YYYYMMDDHHMMSS"
     *
     * @param date the String date "DD.MM.YYYY"
     * @param time the String time "HH:MM"
     * @return a long Formated to fit Firebase
     */
    private long formatDate(String date, String time) {

        String day = date.substring(0, 2);
        String month = date.substring(3, 5);
        String year = date.substring(6, 10);

        String hours = time.substring(0, 2);
        String minutes = date.substring(3, 5);

        //Long.valueOf(year+month+day+hours+minutes+"00").longValue();

        return Long.valueOf(year + month + day + hours + minutes + "00");

    }

}