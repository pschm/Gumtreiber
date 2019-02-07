package de.psst.gumtreiber.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import de.psst.gumtreiber.R;

public class CalendarFragment extends Fragment {

    //TODO ENTFERNEN -> BEISPIEL
    ArrayList<String> appointments = new ArrayList<>();
    private View fragmentView;
    private LinearLayout llAppointmentList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.fragment_calendar, container, false);

        //TODO ERNTFERNEN -> BEISPIEL (WIRD BEI DERBACKNAVIGATION VERDOPPELT)
        appointments.add("3111");
        appointments.add("3222");
        appointments.add("3333");

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //LinearLayout in dem die Terminliste angzeigt wird
        llAppointmentList = getActivity().findViewById(R.id.ll_appointment_list);

        //Floating Action Button stuff
        FloatingActionButton btnAdd = getActivity().findViewById(R.id.fab_add_appointment);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new AppointmentFragment()).addToBackStack(null).commit();
            }
        });

        refreshLinearLayout();


    }


    /**
     * Adds a new Appointment as View to the LinearLayout
     */
    public void addToLinearLayout(final String appointment) {

        LinearLayout appointmentView = new LinearLayout(getActivity());
        appointmentView.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvRoom = new TextView(getActivity());
        tvRoom.setText(appointment);
        tvRoom.setTextSize(16f);

        //Termindaten
        TextView startDate = new TextView(getActivity());
        startDate.setText("01.11.2019 - 09:00");
        TextView endDate = new TextView(getActivity());
        endDate.setText("01.11.2019 - 19:00");

        //Mittleres LinearLayout, welches die Termindaten untereineander ind der Appointmentview anzeigt
        LinearLayout middel = new LinearLayout(getActivity());
        middel.setPadding(32, 0, 32, 8);
        middel.setOrientation(LinearLayout.VERTICAL);
        middel.addView(startDate);
        middel.addView(endDate);

        //Befüllen der AppointmentView
        ImageView btn_remove = new ImageView(getActivity());
        btn_remove.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_remove_24dp));
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO TERMIN WIRKLICH ENTFERNEN
                removeAppointment(appointment);

            }
        });

        appointmentView.addView(btn_remove);
        appointmentView.addView(tvRoom);
        appointmentView.addView(middel);

        llAppointmentList.addView(appointmentView);

    }


    /**
     * Removes anAppointmen and refreshes the LinearLayout afterwards
     *
     * @param c the Appointment that should get removed
     */
    public void removeAppointment(String c) {
        appointments.remove(c);
        refreshLinearLayout();
    }

    /**
     * Refreshes the LinearLayout
     */
    public void refreshLinearLayout() {

        llAppointmentList.removeAllViews();

        for (String ap : appointments) {
            addToLinearLayout(ap);

        }
    }
}
