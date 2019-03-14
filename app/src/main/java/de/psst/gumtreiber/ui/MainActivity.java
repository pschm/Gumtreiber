package de.psst.gumtreiber.ui;


import android.content.Context;
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

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.data.UserDataSync;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.ui.fragments.CalendarFragment;
import de.psst.gumtreiber.ui.fragments.FriendListFragment;
import de.psst.gumtreiber.ui.fragments.MapFragment;
import de.psst.gumtreiber.ui.fragments.SettingsFragment;
import de.psst.gumtreiber.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Fragment Tags
    private static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";
    private static final String CAL_FRAGMENT_TAG = "CALENDAR_FRAGMENT";
    private static final String FND_FRAGMENT_TAG = "FRIEND_LIST_FRAGMENT";
    private static final String STG_FRAGMENT_TAG = "SETTINGS_FRAGMENT";

    //LocationState stuff
    private MainViewModel model;
    private Boolean locationState;
    private MenuItem drawerNavSwitch;
    private MenuItem toolbarDoneBTN;
    private LocationHandler locationHandler;

    //Firebase UserID
    private String uid;

    // UserDataSync
    private UserDataSync uds;

    //Fragment Manager
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private Toast backToast;

    private static Context instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.instance = this;

        //Getting the locationState from the ViewModel
        model = ViewModelProviders.of(this).get(MainViewModel.class);
        locationState = model.getLocationState();

        //Creating a new LocationHandler
        locationHandler = new LocationHandler(this, locationState);

        //Setting Layout and MapFragment
        setContentView(R.layout.activity_main);
        fragmentManager.beginTransaction().add(R.id.content_frame, new MapFragment(), MAP_FRAGMENT_TAG).commit();


        //Setting Uid
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Init Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        resetActionBarTitle();

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
        initDrawerSwitch();

        //TODO updatesEnabled aus config laden
        uds = new UserDataSync(this, getLocationHandler(), false);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        uds.startUpdating();
    }

    public void setActionBarTitle(String s) {
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(s);
    }

    public void resetActionBarTitle() {
        if(getSupportActionBar() == null) return;

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if(u != null) getSupportActionBar().setTitle(getString(R.string.title_greeting_1, u.getDisplayName()));
        else getSupportActionBar().setTitle("");
    }

    public MenuItem getToolbarDoneBTN() {
        return toolbarDoneBTN;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Fragment mapFragment = fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);


        //If the drawer is Open a click on the back button will close it
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //If the Map is Visible the next back button double tap will close the app
        else if(mapFragment != null && mapFragment.isVisible()) {
            if (backToast != null && backToast.getView().getWindowToken() != null) {
                //Getting back to Homescreen Screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            } else {
                backToast = Toast.makeText(this, getString(R.string.double_tap_close), Toast.LENGTH_SHORT);
                backToast.show();
            }
        }
        //If one "main" fragment is open (the ones, which can be directly accessed by the nav drawer), go back top map
        else if(isMainFragmentVisible()) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        //In any other case the last first fragment from the backStack will show
        else fragmentManager.popBackStackImmediate();
    }

    private boolean isMainFragmentVisible() {
        Fragment calendarFgmt = fragmentManager.findFragmentByTag(CAL_FRAGMENT_TAG);
        Fragment friendListFgmt = fragmentManager.findFragmentByTag(FND_FRAGMENT_TAG);
        Fragment settingsFgmt = fragmentManager.findFragmentByTag(STG_FRAGMENT_TAG);

        return (calendarFgmt != null && calendarFgmt.isVisible()) ||
                (friendListFgmt != null && friendListFgmt.isVisible()) ||
                (settingsFgmt != null && settingsFgmt.isVisible());
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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

                fragmentManager.beginTransaction().replace(R.id.content_frame, new CalendarFragment(), CAL_FRAGMENT_TAG).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;


            case (R.id.nav_friendList):

                fragmentManager.beginTransaction().replace(R.id.content_frame, new FriendListFragment(), FND_FRAGMENT_TAG).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;

            case (R.id.nav_settings):

                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsFragment(), STG_FRAGMENT_TAG).addToBackStack(null).commit();

                drawer.closeDrawer(GravityCompat.START);
                break;

        }

        return true;
    }


    public LocationHandler getLocationHandler() {
        return locationHandler;
    }

    public void initDrawerSwitch() {

        //Setting initial icon & Switch state
        ((SwitchCompat) drawerNavSwitch.getActionView()).setChecked(locationState);
        if (!locationState)
            drawerNavSwitch.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_location_off_24dp));

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

    public static Context getInstance() {
        return instance;
    }

    public UserDataSync getUds() {
        return uds;
    }

    public void returnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
