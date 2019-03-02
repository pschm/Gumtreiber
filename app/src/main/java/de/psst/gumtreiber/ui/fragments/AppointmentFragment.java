package de.psst.gumtreiber.ui.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.location.Room;
import de.psst.gumtreiber.ui.MainActivity;
import de.psst.gumtreiber.viewmodels.CalendarViewModel;


public class AppointmentFragment extends Fragment {



    private String uid;
    private GregorianCalendar c = new GregorianCalendar();
    private MainActivity activity;
    private CalendarViewModel model;
    private Spinner spinner;

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd.MM.yyyy");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_appointment, container, false);
        activity = (MainActivity) getActivity();
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        model = ViewModelProviders.of((FragmentActivity) activity).get(CalendarViewModel.class);
        return fragmentView;

    }

    @Override
    public void onDestroyView() {
        activity.getToolbarDoneBTN().setOnMenuItemClickListener(null);
        activity.getToolbarDoneBTN().setVisible(false);
        activity.resetActionBarTitle();
        super.onDestroyView();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Spinner aka DropDownMenu for the Rooms
        initSpinner();

        //Date and TimePickers for start and endDate
        initStartDatePickers();
        initEndDatePickers();

        activity.getToolbarDoneBTN().setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Appointment appointment = buildAppointment();

                if (appointment.getFormatedStartDate() > appointment.getFormatedEndDate()) {
                    Toast checkDate = Toast.makeText(activity, "Zeitreisen sind unmöglich", Toast.LENGTH_SHORT);
                    checkDate.show();
                } else {
                    saveAppointment(appointment);
                    activity.onBackPressed();
                }

                return true;
            }
        });
        activity.getToolbarDoneBTN().setVisible(true);
        activity.setActionBarTitle(getString(R.string.title_create_appointment));

    }


    /**
     * Initiates the TextViews with Date & Time Pickers in a barbaric kind of way
     */
    private void initSpinner() {

        //Spinner for the Rooms
        spinner = activity.findViewById(R.id.spinn_room);
        spinner.setAdapter(new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item, Room.getAllRooms())); //TODO getAllRooms() ggf probleme bei speichern?

    }

    //Building Date and Time Pickers
    /**
     * Initiates the Date and Time Pickers for the end date
     */
    private void initStartDatePickers() {

        //Termin Anfang
        TextView tvStartDate = activity.findViewById(R.id.tv_start_date);
        tvStartDate.setText(DATE_FORMAT.format(c.getTime()));
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

                                Calendar calendar = new GregorianCalendar(year, month, day);
                                tvStartDate.setText(DATE_FORMAT.format(calendar.getTime()));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        TextView tvStartTime = activity.findViewById(R.id.tv_start_time);
        tvStartTime.setText("12:00");
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                TextView tvStartTime = activity.findViewById(R.id.tv_start_time);

                                Calendar calendar = new GregorianCalendar(0,0,0, hourOfDay, minute);
                                tvStartTime.setText(TIME_FORMAT.format(calendar.getTime()));
                            }
                        }, 12, 00, true);
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
        tvEndDate.setText(DATE_FORMAT.format(c.getTime()));
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                TextView tvEndDate = activity.findViewById(R.id.tv_end_date);

                                Calendar calendar = new GregorianCalendar(year, month, day);
                                tvEndDate.setText(DATE_FORMAT.format(calendar.getTime()));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        TextView tvEndTime = activity.findViewById(R.id.tv_end_time);
        tvEndTime.setText("13:00");
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                TextView tvEndTime = activity.findViewById(R.id.tv_end_time);

                                Calendar calendar = new GregorianCalendar(0,0,0, hourOfDay, minute);
                                tvEndTime.setText(TIME_FORMAT.format(calendar.getTime()));
                            }
                        }, 13, 00, true);
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
        Room room = Room.values()[spinner.getSelectedItemPosition()];

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

        String day = date.substring(5, 7);
        String month = date.substring(8, 10);
        String year = date.substring(11, 15);

        String hours = time.substring(0, 2);
        String minutes = time.substring(3, 5);

        return Long.valueOf(year + month + day + hours + minutes + "00");
    }

}