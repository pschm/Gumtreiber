package de.psst.gumtreiber.ui.fragments;

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
        View fragmentView = inflater.inflate(R.layout.fragment_find_friends, container, false);
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
    public void addToLinearLayout(final String b) {

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
    public void refreshLinearLayout() {

        llPeopleList.removeAllViews();
        filterUserList();

        for (String ap : userList) {
            addToLinearLayout(ap);

        }
    }

    /**
     * Filters People who are already on the FrindList out of the UserList
     */
    public void filterUserList() {

        getFriendsList();
        for (String s : friendlist) {
            userList.remove(s);
        }

    }


    /**
     * Gets the FriendList from SharedPreferences
     */
    public void getFriendsList() {
        friendlist = getActivity().getApplicationContext().getSharedPreferences("FriendList", Context.MODE_PRIVATE).getStringSet("friendList", def);
    }

}
