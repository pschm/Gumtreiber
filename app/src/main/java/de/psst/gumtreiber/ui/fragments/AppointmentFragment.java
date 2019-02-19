package de.psst.gumtreiber.ui.fragments;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.location.Room;
import de.psst.gumtreiber.viewmodels.CalendarViewModel;


public class AppointmentFragment extends Fragment {

    //Date formats & calendar
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat READABLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat READABLE_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private Calendar c = Calendar.getInstance();


    private CalendarViewModel model;
    private Activity activity;
    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_appointment, container, false);
        activity = getActivity();
        model = ViewModelProviders.of((FragmentActivity) activity).get(CalendarViewModel.class);
        return fragmentView;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Spinner aka DropDownMenu for the Rooms
        spinner = activity.findViewById(R.id.spinn_room);
        spinner.setAdapter(new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item, Room.values()));

        //Date and TimePickers for start and endDate
        initStartDatePickers();
        initEndDatePickers();

        //SaveButton
        Button btnSave = activity.findViewById(R.id.btn_submit_appointment);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Appointment appointment = buildAppointment();

                if (appointment.getFormattedStartDate() > appointment.getFormattedEndDate()) {
                    Toast checkDate = Toast.makeText(activity, "Zeitreisen sind unmöglich", Toast.LENGTH_SHORT);
                    checkDate.show();
                } else {
                    saveAppointment(appointment);
                    activity.onBackPressed();
                }
            }
        });

    }


    //Building Date and Time Pickers

    /**
     * Initiates the Date and Time Pickers for the end date
     */
    private void initStartDatePickers() {

        //Termin Anfang
        TextView tvStartDate = activity.findViewById(R.id.tv_start_date);
        tvStartDate.setText(READABLE_DATE_FORMAT.format(c.getTime()));
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
        tvStartTime.setText(READABLE_TIME_FORMAT.format(c.getTime()));
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

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
    }

    /**
     * Initiates the Date and Time Pickers for the end date
     */
    private void initEndDatePickers() {
        //Termin Ende
        TextView tvEndDate = activity.findViewById(R.id.tv_end_date);
        tvEndDate.setText(READABLE_DATE_FORMAT.format(c.getTime()));
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
        tvEndTime.setText(READABLE_TIME_FORMAT.format(c.getTime()));
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

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
    }


    //Appointment building and saving

    /**
     * Builds an appointment object with the Values of the
     * TextViews: tv_start_ date, tv_start_ time, tv_end_ date, tv_end_ time
     * and the room from the spinner spinn_room
     * @return an Appointment object with the chosen values
     */
    private Appointment buildAppointment() {

        //Room
        Room room = (Room) spinner.getSelectedItem();

        //Start Date
        TextView tvStartDate = activity.findViewById(R.id.tv_start_date);
        TextView tvStartTime = activity.findViewById(R.id.tv_start_time);

        long startDate = formatDate(tvStartDate.getText().toString(), tvStartTime.getText().toString());


        //End Date
        TextView tvEndDate = activity.findViewById(R.id.tv_end_date);
        TextView tvEndTime = activity.findViewById(R.id.tv_end_time);

        long endDate = formatDate(tvEndDate.getText().toString(), tvEndTime.getText().toString());


        return new Appointment(startDate, endDate, room);
    }

    /**
     * Delegates the Saving og the Appointment to the ViewModel
     *
     * @param appointment the appointment object to save
     */
    private void saveAppointment(Appointment appointment) {
        model.saveAppointment(appointment);
    }

    //Date formatting

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
        String minutes = time.substring(3, 5);

        return Long.valueOf(year + month + day + hours + minutes + "00");
    }

    /**
     * Formats a date given as integers (day,month,year) to a String with the dd.mm.yyyy format
     * @param day day
     * @param month month
     * @param year year a
     * @return Formatted date String
     */
    private String getReadableDate(int day, int month, int year) {

        Calendar pickedDate = Calendar.getInstance();
        pickedDate.set(year, month, day);

        return READABLE_DATE_FORMAT.format(pickedDate.getTime());
    }

    /**
     * Formats a time given as integers (hour,minutes) to a String with the hh:mm format
     * @param hour hour
     * @param minute minute
     * @return Formatted time String
     */
    private String getReadableTime(int hour, int minute) {

        Calendar pickedDate = Calendar.getInstance();
        //Leaving the date values as they are - we are only interested in the hours and minutes
        pickedDate.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hour, minute);

        return READABLE_TIME_FORMAT.format(pickedDate.getTime());
    }



}