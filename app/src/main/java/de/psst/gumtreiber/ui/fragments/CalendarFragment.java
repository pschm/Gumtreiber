package de.psst.gumtreiber.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.ui.CalendarAdapter;
import de.psst.gumtreiber.viewmodels.CalendarViewModel;

public class CalendarFragment extends Fragment {

    private Activity activity;
    private CalendarViewModel model;

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;


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

        //Init Floating Action Button
        FloatingActionButton btnAdd = activity.findViewById(R.id.fab_add_appointment);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAppointmentFragment();
            }
        });
    }

    private void startAppointmentFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AppointmentFragment()).addToBackStack(null).commit();
        }
    }



}
