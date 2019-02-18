package de.psst.gumtreiber.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.viewmodels.CalendarViewModel;

//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import de.psst.gumtreiber.ui.CalendarAdapter;

public class CalendarFragment extends ListFragment {

    private Activity activity;
    private CalendarViewModel model;
    private CalendarListAdapter adapter;
    //RecyclerView
    //private RecyclerView recyclerView;
    //private RecyclerView.Adapter adapter;
    //private RecyclerView.LayoutManager manager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        activity = getActivity();

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init ViewModel
        model = new CalendarViewModel(activity.getApplication());

        //ListView
        adapter = new CalendarListAdapter(activity, R.layout.recycler_view_calendar_row_layout, model.getAppointments());
        setListAdapter(adapter);



        /*
        //Wenn Recycler View wieder genutzt werden soll -> Layout.xml + vererbung anpassen !!!
        //Init RecyclerView
        recyclerView = activity.findViewById(R.id.rv_calendar);

        //use a LinearLayout manager
        manager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(manager);

        //specify an adapter
        if (getContext() != null) {
            adapter = new CalendarAdapter(model, getContext());
            recyclerView.setAdapter(adapter);
        }
        */


        //Init Floating Action Button
        FloatingActionButton btnAdd = activity.findViewById(R.id.fab_add_appointment);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAppointmentFragment();
            }
        });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Appointment clickedAppointment = model.getAppointments().get(position);

        //TODO Über die ID's Gehen!
        //AltertDialog für Abfrage ob wirklich gelösht werden soll
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Termin entfernen ?");

        alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                model.removeAppointment(clickedAppointment);
                adapter.refreshEvents(model.getAppointments());
            }
        });

        alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    private void startAppointmentFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AppointmentFragment()).addToBackStack(null).commit();
        }
    }



}

class CalendarListAdapter extends ArrayAdapter<Appointment> {

    private int resourceLayout;
    private Context mContext;
    private List<Appointment> appointments;

    public CalendarListAdapter(Context context, int resource, List<Appointment> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.appointments = items;
    }

    public void refreshEvents(List<Appointment> appointments) {
        this.appointments.clear();
        this.appointments.addAll(appointments);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Appointment p = getItem(position);

        if (p != null) {
            TextView room = v.findViewById(R.id.rv_tv_room);
            TextView startDate = v.findViewById(R.id.rv_tv_start_date);
            TextView endDate = v.findViewById(R.id.rv_tv_end_date);

            if (room != null) {
                room.setText(p.getRoom().toString());
            }

            if (startDate != null) {
                startDate.setText(p.getStringDate(p.getFormatedStartDate()));
            }

            if (endDate != null) {
                endDate.setText(p.getStringDate(p.getFormatedEndDate()));
            }
        }

        return v;
    }

}