package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.ui.MainActivity;

public class FindFriendsFragment extends Fragment {

    private MainActivity activity;

    //RecyclerView
    private RecyclerView recyclerView;
    private FindFriendsFragment.RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainActivity) getActivity();
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

        recyclerView = getActivity().findViewById(R.id.friends_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("ABC", "Der fast kopflose Kohls")); //TODO init korrekt machen
        userList.add(new User("ABC", "Yggi der Elf"));
        userList.add(new User("ABC", "Prof. Flitvikor"));
        userList.add(new User("ABC", "Die bittere Birgit"));

        adapter = new RecyclerAdapter(this, userList);
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

            holder.txtUserName.setText(user.name);

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Freund hinzufügen
                }

            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public void refresh() {
            notifyDataSetChanged();
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





/* //TODO alten code löschen
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;

public class FindFriendsFragment extends Fragment {

    private View fragmentView;
    private LinearLayout llPeopleList;
    private Set<String> def = new TreeSet<>();
    private Set<String> friendlist;
    private SharedPreferences.Editor editor;

    //TODO Erstetzten durch echte user liste
    private Set<String> userList = new TreeSet<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.fragment_find_friends, container, false);
        //TODO ERNTFERNEN -> BEISPIEL
        userList.add("Der fast kopflose Kohls");
        userList.add("Yggi der Elf");
        userList.add("Prof. Flitvikor");
        userList.add("Die bittere Birgit");

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editor = getActivity().getApplicationContext().getSharedPreferences("FriendList", Context.MODE_PRIVATE).edit();

        getFriendsList();
        //LinearLayout in dem die Terminliste angzeigt wird
        llPeopleList = (LinearLayout) getActivity().findViewById(R.id.ll_people_list);
        refreshLinearLayout();
    }


    /**
     * Adds a new small new LinearLayout as View to the main LinearLayout
     */
 /*   public void addToLinearLayout(final String b) {

        //TODO Leute richtig anzeigen ++ Schön machen
        LinearLayout smallLayout = new LinearLayout(getActivity());
        smallLayout.setOrientation(LinearLayout.HORIZONTAL);

        final TextView tvName = new TextView(getActivity());
        tvName.setTextSize(16f);
        tvName.setText(b);

        ImageView btnAddFriend = new ImageView(getActivity());
        btnAddFriend.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add2_24dp));
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFriendsList();
                friendlist.add(b);
                editor.remove("friendList").commit();
                editor.putStringSet("friendList", friendlist).apply();

            }
        });


        //TODO ALLE BUTTONS AM RECHTEN RAND ANZEIGEN
        smallLayout.addView(btnAddFriend);
        smallLayout.addView(tvName);
        llPeopleList.addView(smallLayout, llPeopleList.getWidth());

    }

    /**
     * Refreshes the LinearLayout
     */
 /*   public void refreshLinearLayout() {

        llPeopleList.removeAllViews();
        filterUserList();

        for (String ap : userList) {
            addToLinearLayout(ap);

        }
    }

    /**
     * Filters People who are already on the FrindList out of the UserList
     */
/*    public void filterUserList() {

        getFriendsList();
        for (String s : friendlist) {
            userList.remove(s);
        }

    }


    /**
     * Gets the FriendList from SharedPreferences
     */
 /*   public void getFriendsList() {
        friendlist = getActivity().getApplicationContext().getSharedPreferences("FriendList", Context.MODE_PRIVATE).getStringSet("friendList", def);
    }

}
*/