package de.psst.gumtreiber.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.viewmodels.CalendarViewModel;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private CalendarViewModel model;
    private Context context;
    private ArrayList<Appointment> appointments;

    public CalendarAdapter(CalendarViewModel model, @NonNull Context context) {
        this.model = model;
        this.context = context;
        this.appointments = model.getAppointments();
    }

    @NonNull
    @Override
    public CalendarAdapter.CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_calendar_row_layout, parent, false);
        v.setOnClickListener(new CalendarRecyclerViewOnClickListener());

        CalendarViewHolder vh = new CalendarViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.rv_room.setText(appointments.get(position).getRoom().getName());
        holder.rv_endDate.setText(appointments.get(position).getStringDate(appointments.get(position).getFormatedEndDate()));
        holder.rv_startDate.setText(appointments.get(position).getStringDate(appointments.get(position).getFormatedStartDate()));

    }


    @Override
    public int getItemCount() {
        return appointments.size();
    }


    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        public TextView rv_room, rv_startDate, rv_endDate;


        public CalendarViewHolder(View v) {
            super(v);

            rv_room = v.findViewById(R.id.rv_tv_room);
            rv_startDate = v.findViewById(R.id.rv_tv_start_date);
            rv_endDate = v.findViewById(R.id.rv_tv_end_date);

        }

    }

    private class CalendarRecyclerViewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            //TODO NullPointerExeption Beheben !!!
            RecyclerView calendarRecyclerView = (RecyclerView) view.findViewById(R.id.rv_calendar);
            RecyclerView.ViewHolder holder = calendarRecyclerView.getChildViewHolder(view);
            int itemPosition = holder.getAdapterPosition();
            Log.v("POSITION", itemPosition + "");

            //AltertDialog für Abfrage ob wirklich gelösht werden soll
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Termin entfernen ?");

            alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    model.removeAppointment();
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
    }
}


