package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.ui.MainActivity;
import de.psst.gumtreiber.viewmodels.FriendsViewModel;

public class FindFriendsFragment extends Fragment {

    private MainActivity activity;

    //RecyclerView
    private RecyclerView recyclerView;
    private FindFriendsFragment.RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    //ViewModel
    private FriendsViewModel model;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = Objects.requireNonNull((MainActivity) getActivity());
        model = ViewModelProviders.of(activity).get(FriendsViewModel.class);
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }

    @Override
    public void onDestroyView() {
        activity.resetActionBarTitle();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setActionBarTitle(getString(R.string.title_add_friends));

        recyclerView = activity.findViewById(R.id.friends_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        /*ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("ABC", "Der fast kopflose Kohls"));
        userList.add(new User("ABC", "Yggi der Elf"));
        userList.add(new User("ABC", "Prof. Flitvikor"));
        userList.add(new User("ABC", "Die bittere Birgit"));
        */
        adapter = new RecyclerAdapter(this, new ArrayList<>(model.getFilterdUserList()));
        recyclerView.setAdapter(adapter);

    }



    /**
     * Recycle-bin-Adapter
     */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private FindFriendsFragment fragment;
        private ArrayList<User> dataset;

        private RecyclerAdapter(FindFriendsFragment fragment, ArrayList<User> dataset) {
            this.fragment = fragment;
            this.dataset = dataset;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_addfriend, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final User user = dataset.get(position);

            holder.txtUserName.setText(user.getName());

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model.addFriend(user);
                    activity.onBackPressed();
                }

            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }


        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtUserName;
            private Button btnAdd;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                txtUserName = itemView.findViewById(R.id.txtUserName);
                btnAdd = itemView.findViewById(R.id.btnAddFriend);
            }
        }
    }
}