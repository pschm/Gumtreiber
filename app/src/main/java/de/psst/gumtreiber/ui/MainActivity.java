package de.psst.gumtreiber.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.ui.fragments.CalendarFragment;
import de.psst.gumtreiber.ui.fragments.MapFragment;
import de.psst.gumtreiber.viewmodels.MainViewModel;

//import de.psst.gumtreiber.ui.fragments.FriendListFragment;
//import de.psst.gumtreiber.ui.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //LocationState stuff
    private MainViewModel model;
    private Boolean locationState;
    private LocationHandler locationHandler;

    //Firebase UserID
    private String uid;

    //Fragment Manager & Fragments
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initailizing Fragments
        fragmentManager = getSupportFragmentManager();


        //Setting Layout and MapFragment
        setContentView(R.layout.activity_main);
        fragmentManager.beginTransaction().add(R.id.content_frame, new MapFragment()).commit();

        //Getting the locationState from the ViewModel
        model = new MainViewModel(getApplication());
        locationState = model.getLocationState();

        //Setting Uid
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //Creating a new LocationHandler
        locationHandler = new LocationHandler(this, locationState);

        //TODO Hier das Icon für den Standort setzen
        //Init Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            fragmentManager.popBackStackImmediate();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {

            case (R.id.nav_location):

                if (locationState) {
                    //Changing Icon
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location_off_24dp));
                    //disable GPS
                    locationHandler.disableUpdates();
                    //activate TimeSchedule on Firebase
                    Firebase.activateSchedule(uid);
                    //changing the shared preference
                    model.setLocationState(false);
                    locationState = model.getLocationState();
                    Log.v("LOCATION STATE", locationState.toString());


                } else {
                    //Changing Icon
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location_on_24dp));
                    //disable GPS
                    locationHandler.enableUpdates();
                    //activate TimeSchedule on Firebase
                    Firebase.deactivateSchedule(uid);
                    //changing the shared preference
                    model.setLocationState(true);
                    locationState = model.getLocationState();
                    Log.v("LOCATION STATE", locationState.toString());

                }
                break;

            case R.id.nav_calendar:

                fragmentManager.beginTransaction().replace(R.id.content_frame, new CalendarFragment()).addToBackStack(null).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            /*
            case (R.id.nav_friendList):

                fragmentManager.beginTransaction().replace(R.id.content_frame, new FriendListFragment()).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;

            case (R.id.nav_settings):

                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;
            */
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //TODO menu.findMenuItem(...) wirft nullPoniterExeption ... aber warum ?
        //if (!locationState) menu.findItem(R.id.nav_location).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location_off_24dp));
        return super.onPrepareOptionsMenu(menu);
    }
}
