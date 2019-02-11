package de.psst.gumtreiber.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;


public class AppointmentFragment extends Fragment {


    private GregorianCalendar c = new GregorianCalendar();
    private View fragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.fragment_appointment, container, false);
        return fragmentView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initTextViews();

        Button btnSubmitAppointment = getActivity().findViewById(R.id.btn_submit_appointment);
        btnSubmitAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Termin Speichern

                getActivity().onBackPressed();
            }
        });
    }


    /**
     * Initiates the TextViews with Date & Time Pickers in a barbaric kind of way
     */
    public void initTextViews() {

        //Termin Anfang
        TextView tvStartDate = getView().findViewById(R.id.tv_start_date);
        tvStartDate.setText(getCurrentDate());
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                TextView tvStartDate = getActivity().findViewById(R.id.tv_start_date);
                                tvStartDate.setText(getFancyDate(day, month, year));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        TextView tvStartTime = getView().findViewById(R.id.tv_start_time);
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
                                TextView tvStartTime = getActivity().findViewById(R.id.tv_start_time);
                                tvStartTime.setText(getFancyTime(hourOfDay, minute));
                            }
                        }, hourOfDay, minute, true);
                timePickerDialog.show();
            }
        });


        //Termin Ende
        TextView tvEndDate = getView().findViewById(R.id.tv_end_date);
        tvEndDate.setText(getCurrentDate());
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                TextView tvStartDate = getActivity().findViewById(R.id.tv_end_date);
                                tvStartDate.setText(getFancyDate(day, month, year));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        TextView tvEndTime = getView().findViewById(R.id.tv_end_time);
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
                                TextView tvEndTime = getActivity().findViewById(R.id.tv_end_time);
                                tvEndTime.setText(getFancyTime(hourOfDay, minute));
                            }
                        }, hourOfDay, minute, true);
                timePickerDialog.show();
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

        return getFancyDate(day, month, year);

    }


    /**
     * Formats a date given as integers (day,month,year) to a String with the dd.mm.yyyy format
     *
     * @param day
     * @param month
     * @param year
     * @return Formatted date String
     */
    private String getFancyDate(int day, int month, int year) {

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

        return getFancyTime(hour, minute);

    }


    /**
     * Formats a time given as integers (hour,minutes) to a String with the hh:mm format
     *
     * @param hour
     * @param minute
     * @return Formatted time String
     */
    private String getFancyTime(int hour, int minute) {

        String sHour = Integer.toString(hour);
        String sMinute = Integer.toString(minute);

        if (Integer.parseInt(sHour) <= 9) sHour = "0" + sHour;
        if (Integer.parseInt(sMinute) <= 9) sMinute = "0" + sMinute;

        return sHour + ":" + sMinute;
    }


}