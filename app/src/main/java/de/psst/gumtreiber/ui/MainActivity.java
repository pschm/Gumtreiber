package de.psst.gumtreiber.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.ui.fragments.CalendarFragment;
import de.psst.gumtreiber.ui.fragments.FriendListFragment;
import de.psst.gumtreiber.ui.fragments.MapFragment;
import de.psst.gumtreiber.ui.fragments.SettingsFragment;
import de.psst.gumtreiber.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Fragment Tags
    String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";

    //LocationState stuff
    private MainViewModel model;
    private Boolean locationState;
    private MenuItem drawerNavSwitch;
    private MenuItem toolbarDoneBTN;
    private LocationHandler locationHandler;

    //Firebase UserID
    private String uid;

    //Fragment Manager
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private Toast backtoast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting the locationState from the ViewModel
        model = new MainViewModel(getApplication());
        locationState = model.getLocationState();

        //Creating a new LocationHandler
        locationHandler = new LocationHandler(this, locationState);

        //Setting Layout and MapFragment
        setContentView(R.layout.activity_main);
        fragmentManager.beginTransaction().add(R.id.content_frame, new MapFragment(), MAP_FRAGMENT_TAG).commit();


        //TODO Hier das Icon für den Standort setzen
        //Init Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Setting Uid
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Listener for the location switch
        drawerNavSwitch = navigationView.getMenu().findItem(R.id.nav_location);
        ((SwitchCompat) drawerNavSwitch.getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (locationState) {
                    //Changing Icon
                    drawerNavSwitch.setIcon(ContextCompat.getDrawable(buttonView.getContext(), R.drawable.ic_location_off_24dp));
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
                    drawerNavSwitch.setIcon(ContextCompat.getDrawable(buttonView.getContext(), R.drawable.ic_location_on_24dp));
                    //disable GPS
                    locationHandler.enableUpdates();
                    //activate TimeSchedule on Firebase
                    Firebase.deactivateSchedule(uid);
                    //changing the shared preference
                    model.setLocationState(true);
                    locationState = model.getLocationState();
                    Log.v("LOCATION STATE", locationState.toString());

                }
            }
        });

    }

    public void setActionBarTitle(String s) {
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(s);
    }

    public void resetActionBarTitle() {
        if(getSupportActionBar() == null) return;

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if(u != null) getSupportActionBar().setTitle(u.getDisplayName());
        else getSupportActionBar().setTitle("");
    }

    public MenuItem getToolbarDoneBTN() {
        return toolbarDoneBTN;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);

        //If the drawer is Open a click on the back button will close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //If the Map is Visible the next back button double tap will close the app
        else if (mapFragment != null && mapFragment.isVisible()) {
            if (backtoast != null && backtoast.getView().getWindowToken() != null) {
                //Getting back to Homescreen Screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            } else {
                backtoast = Toast.makeText(this, "Doppelklicken um App zu beenden", Toast.LENGTH_SHORT);
                backtoast.show();
            }
        }
        //In any other case the last first fragment from the backStack will show
        else fragmentManager.popBackStackImmediate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        toolbarDoneBTN = menu.findItem(R.id.btn_action_done);
        toolbarDoneBTN.setVisible(false);
        return super.onCreateOptionsMenu(menu);
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

                //Toggle drawer switch. This will call the onCheckedChangeListener from the switch
                ((SwitchCompat) drawerNavSwitch.getActionView()).setChecked(!locationState);
                break;

            case R.id.nav_calendar:

                fragmentManager.beginTransaction().replace(R.id.content_frame, new CalendarFragment()).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;


            case (R.id.nav_friendList):

                fragmentManager.beginTransaction().replace(R.id.content_frame, new FriendListFragment()).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;

            case (R.id.nav_settings):

                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;

        }

        return true;
    }


    public LocationHandler getLocationHandler() {
        return locationHandler;
    }

}
