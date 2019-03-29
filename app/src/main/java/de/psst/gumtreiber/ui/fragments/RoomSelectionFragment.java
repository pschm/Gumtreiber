package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.location.Room;
import de.psst.gumtreiber.ui.MainActivity;
import de.psst.gumtreiber.viewmodels.CalendarViewModel;

public class RoomSelectionFragment extends Fragment {

    private MainActivity activity;
    private CalendarViewModel model;
    private RecyclerAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_select_room, container, false);
        activity = (MainActivity) getActivity();
        model = ViewModelProviders.of(activity).get(CalendarViewModel.class);
        return fragmentView;

    }

    @Override
    public void onDestroyView() {
        activity.resetActionBarTitle();
        super.onDestroyView();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setActionBarTitle(getString(R.string.title_select_room));

        //RecyclerView
        RecyclerView recyclerView = activity.findViewById(R.id.select_room_view);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new RecyclerAdapter(Arrays.asList(Room.values()));
        recyclerView.setAdapter(adapter);


        //Add Search-Functionality
        EditText txtSearch = activity.findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filterRooms(s.toString());
            }
        });

    }


    /**
     * Recycle-bin-Adapter
     */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private List<Room> dataset;

        RecyclerAdapter(List<Room> dataset) {
            this.dataset = new ArrayList<>(dataset);
        }

        @NonNull
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_room, parent, false);
            return new RecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
            final Room room = dataset.get(position);

            holder.txtRoom.setText( room.toString() );

            holder.txtRoom.setOnClickListener(v -> {
                model.selectedRoom = room;
                activity.onBackPressed();
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        void refresh() {
            notifyDataSetChanged();
        }

        public void filterRooms(String s) {
            dataset.clear();

            for (Room room : Room.values()) {
                if(room.name().toUpperCase().contains(s.toUpperCase())) {
                    dataset.add(room);
                    continue;
                }

                if(room.toString().toUpperCase().contains(s.toUpperCase())) {
                    dataset.add(room);
                }
            }
            refresh();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtRoom;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                txtRoom = itemView.findViewById(R.id.txtRoom);
            }
        }
    }
}

