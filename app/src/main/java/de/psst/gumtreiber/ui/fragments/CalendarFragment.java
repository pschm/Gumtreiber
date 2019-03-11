package de.psst.gumtreiber.ui.fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.location.Room;
import de.psst.gumtreiber.ui.MainActivity;
import de.psst.gumtreiber.viewmodels.CalendarViewModel;

public class CalendarFragment extends Fragment {

    private MainActivity activity;
    private CalendarViewModel model;
    private RecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onDestroyView() {
        activity.resetActionBarTitle();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setActionBarTitle(getString(R.string.title_appointment_overview));

        //Init ViewModel
        model = new CalendarViewModel(activity.getApplication());


        //RecyclerView
        RecyclerView recyclerView = activity.findViewById(R.id.calendar_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new RecyclerAdapter(this, model.getAppointments());
        recyclerView.setAdapter(adapter);


        //Init Floating Action Button
        FloatingActionButton btnAdd = activity.findViewById(R.id.fab_add_appointment);
        btnAdd.setOnClickListener(view1 -> startAppointmentFragment());
    }


    private void startAppointmentFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AppointmentFragment()).addToBackStack(null).commit();
        }
    }


    private void deleteAppointmentConfirmation(final Appointment appointment) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.popup_rem_appo_title));

        alertDialogBuilder.setPositiveButton(getString(R.string.popup_yes), (dialogInterface, i) -> {
            model.removeAppointment(appointment);
            adapter.refresh();
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.popup_no), (dialogInterface, i) -> dialogInterface.cancel());
        alertDialogBuilder.show();
    }


    /**
     * Recycle-bin-Adapter
     */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private CalendarFragment fragment;
        private ArrayList<Appointment> dataset;

        RecyclerAdapter(CalendarFragment fragment, ArrayList<Appointment> dataset) {
            this.fragment = fragment; //used for popup on deletion
            this.dataset = dataset;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_appointment2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Appointment ap = dataset.get(position);

            holder.txtRoomNbr.setText(ap.getRoom().getNumberDot());
            holder.txtRoomName.setText(ap.getRoom().getName());
            tintRoomCircle(holder.imgRoomCircle, ap.getRoom());

            holder.txtStartDay.setText(getString(R.string.appo_from_dayname, ap.getStartDate().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())) );
            holder.txtStartTime.setText(getString(R.string.appo_from_datetime, ap.getReadableStartDate(), ap.getReadableStartTime()) );

            holder.txtEndDay.setText(getString(R.string.appo_to_dayname, ap.getEndDate().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())) );
            holder.txtEndTime.setText(getString(R.string.appo_to_datetime, ap.getReadableEndDate(), ap.getReadableEndTime()) );


            holder.btnDelete.setOnClickListener(v -> {
                Log.d("RecyclerAdapter", "Delete appointment in room '" + dataset.get(position).getRoom().name() + "'.");
                fragment.deleteAppointmentConfirmation(ap);
            });
        }

        private void tintRoomCircle(ImageView imageView, Room room) {
            int colorResId;
            switch (room.getBuildingNumber()) {
                case "1":
                    colorResId = R.color.colorBuildingNbr1;
                    break;

                case "2":
                    colorResId = R.color.colorBuildingNbr2;
                    break;

                case "3":
                    colorResId = R.color.colorBuildingNbr3;
                    break;

                case "4":
                    colorResId = R.color.colorBuildingNbr4;
                    break;

                case "5":
                    colorResId = R.color.colorBuildingNbr5;
                    break;

                default:
                    colorResId = R.color.colorPrimary;
                    break;
            }

            imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), colorResId));
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        void refresh() {
            notifyDataSetChanged();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtRoomNbr, txtRoomName, txtStartDay, txtStartTime, txtEndDay, txtEndTime;
            private Button btnDelete;
            private ImageView imgRoomCircle;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                txtRoomNbr = itemView.findViewById(R.id.txtRoomNbr);
                txtRoomName = itemView.findViewById(R.id.txtRoomName);
                btnDelete = itemView.findViewById(R.id.btnDeleteAppointment);
                imgRoomCircle = itemView.findViewById(R.id.circle_image);

                txtStartDay = itemView.findViewById(R.id.txtStartName);
                txtStartTime = itemView.findViewById(R.id.txtStartTime);

                txtEndDay = itemView.findViewById(R.id.txtEndName);
                txtEndTime = itemView.findViewById(R.id.txtEndTime);
            }
        }
    }
}