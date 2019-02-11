package de.psst.gumtreiber.ui;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import de.psst.gumtreiber.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TODO Wirklich abfragen & irgendwie beim start setzten !!!
    private boolean visibilityState, locationState;
    private FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //HIer die Icons Setzten

        setContentView(R.layout.activity_main);
        fragmentManager.beginTransaction().add(R.id.content_frame, new MapFragment()).commit();
        //android.R.attr.actionBarSize -> actionbar größe auslesen Standard 56dp

        //Toolbar stuff
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().hide();


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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {

            case (R.id.nav_visibility):

                if (visibilityState) {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_visibility_off_24dp));
                    visibilityState = false;
                } else {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_visibility_on_24dp));
                    visibilityState = true;
                }
                break;

            case (R.id.nav_location):

                if (locationState) {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location_off_24dp));
                    locationState = false;
                } else {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location_on_24dp));
                    locationState = true;
                }
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
}
