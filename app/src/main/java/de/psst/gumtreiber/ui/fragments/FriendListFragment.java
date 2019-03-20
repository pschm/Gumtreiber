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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
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
        model = ViewModelProviders.of(activity).get(FriendsViewModel.class);
        //ArrayList<String> Friends = new ArrayList<User>(model.getFriendListRef().getValue());

        recyclerView = activity.findViewById(R.id.friends_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new RecyclerAdapter(this, model.getFriendListRef());
        recyclerView.setAdapter(adapter);


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

        //AltertDialog für Abfrage ob wirklich gelösht werden soll
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle( getString(R.string.popup_del_friend,user.getName()) );

        alertDialogBuilder.setPositiveButton(getString(R.string.popup_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                model.deleteFriend(user);
                adapter.refresh();
            }
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.popup_abort), new DialogInterface.OnClickListener() {
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
        private List<User> dataset;

        private RecyclerAdapter(FriendListFragment fragment, List<User> dataset) {
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

            holder.txtFriendName.setText(user.getName());
            tintStatusCircle(holder.imgStatusCircle, user);

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.removeFriend(user);
                }
            });
        }

        private void tintStatusCircle(ImageView imageView, User user) {

            if(user.isExpired()) {
                imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.friendOffline));
            } else {
                imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.friendOnline));
            }

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
