package de.psst.gumtreiber.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.ui.MainActivity;
import de.psst.gumtreiber.viewmodels.FriendsViewModel;

public class FriendListFragment extends Fragment {

    private MainActivity activity;
    private FriendsViewModel model;

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_friendlist, container, false);
    }

    @Override
    public void onDestroyView() {
        activity.resetActionBarTitle();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setActionBarTitle(getString(R.string.title_my_friends));

        //Init ViewModel
        model = new FriendsViewModel(getActivity().getApplication());

        recyclerView = getActivity().findViewById(R.id.friends_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        ArrayList<User> test = new ArrayList<>(); //TODO init korrekt machen
        test.add(new User("ABC","Manni"));
        test.add(new User("DEF","Waißnich"));
        adapter = new RecyclerAdapter(this, test);
        recyclerView.setAdapter(adapter);

        //Observe friend list
        model.getFriends().observe(activity, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> friends) {
                adapter.refresh();
            }
        });

        //Button der uns zu der Auswahl möglich Freunde bringt
        FloatingActionButton btnAdd = activity.findViewById(R.id.fab_add_friend);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new FindFriendsFragment()).addToBackStack(null).commit();
                }
            }
        });

    }

    private void removeFriend(final User user) {
        //TODO Über die ID's Gehen!

        //AltertDialog für Abfrage ob wirklich gelösht werden soll
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("\"" + user.name + "\" aus Freundesliste entfernen ?");

        alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                model.deleteFriend(user.name);
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



    /**
     * Recycle-bin-Adapter
     */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private FriendListFragment fragment;
        private ArrayList<User> dataset;

        private RecyclerAdapter(FriendListFragment fragment, ArrayList<User> dataset) {
            this.fragment = fragment;
            this.dataset = dataset;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_friend, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final User user = dataset.get(position);

            holder.txtFriendName.setText(user.name);
            tintStatusCircle(holder.imgStatusCircle, user);

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.removeFriend(user);
                }
            });
        }

        private void tintStatusCircle(ImageView imageView, User user) {
            int colorResId;
            /*
            switch (user.expirationDate) { //TODO
                case "1":
                    colorResId = R.color.colorBuildingNbr1;
                    break;

                case "2":
                    colorResId = R.color.colorBuildingNbr2;
                    break;

                default:
                    colorResId = R.color.colorPrimary;
                    break;
            }

            imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), colorResId));
            */
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public void refresh() {
            notifyDataSetChanged();
        }


        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtFriendName;
            private Button btnRemove;
            private ImageView imgStatusCircle;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                txtFriendName = itemView.findViewById(R.id.txtFriendName);
                btnRemove = itemView.findViewById(R.id.btnRemoveFriend);
                imgStatusCircle = itemView.findViewById(R.id.imgStatusCircle);
            }
        }

    }
}

/* TODO alten code löschen
import android.app.Activity;
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
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_friendlist, container, false);

        activity = getActivity();

        model = ViewModelProviders.of((FragmentActivity) activity).get(FriendsViewModel.class);
        model.getFriends().observe((FragmentActivity) activity, new Observer<List<String>>() {

            @Override
            public void onChanged(List<String> friends) {

                adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, friends);
                setListAdapter(adapter);
            }
        });
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Button der uns zu der Auswahl möglich Freunde bringt
        FloatingActionButton btnAdd = activity.findViewById(R.id.fab_add_friends);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new FindFriendsFragment()).addToBackStack(null).commit();
                }
            }
        });
    }


    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView clickedTextView = v.findViewById(android.R.id.text1);
        final String name = clickedTextView.getText().toString();

        //TODO Über die ID's Gehen!

        //AltertDialog für Abfrage ob wirklich gelösht werden soll
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
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
*/