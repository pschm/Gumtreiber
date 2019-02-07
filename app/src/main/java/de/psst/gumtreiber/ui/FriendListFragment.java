package de.psst.gumtreiber.ui;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.viewmodels.FriendsViewModel;

public class FriendListFragment extends ListFragment {

    //RecyclerView einfügen ??? -> vielleicht

    private FriendsViewModel model;
    private ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_friendlist, container, false);


        model = ViewModelProviders.of((FragmentActivity) getActivity()).get(FriendsViewModel.class);
        model.getFriends().observe(getActivity(), new Observer<List<String>>() {

            @Override
            public void onChanged(List<String> friends) {

                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, friends);
                setListAdapter(adapter);
            }
        });
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Button der uns zu der Auswahl möglich Freunde bringt
        FloatingActionButton btnAdd = getActivity().findViewById(R.id.fab_add_friends);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FindFriendsFragment()).addToBackStack(null).commit();
            }
        });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView clickedTextView = v.findViewById(android.R.id.text1);
        final String name = clickedTextView.getText().toString();

        //TODO Über die ID's Gehen!

        //AltertDialog für Abfrage ob wirklich gelösht werden soll
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("\"" + name + "\" aus Freundesliste entfernen ?");

        alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                model.deleteFriend(name);
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
