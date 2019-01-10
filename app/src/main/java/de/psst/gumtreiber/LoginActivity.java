package de.psst.gumtreiber;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.psst.gumtreiber.data.UserDataSynchronizer;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MapView map = findViewById(R.id.map);
        //Firebase.createUser("123","Max");
        //Firebase.setCurrentLocation("123",7.563138,51.024232,  0);
        //map.setUserList(Firebase.getAllUsers());

        // enable zoom effect
        MapControl mc = new MapControl(map, true);
        mc.setMapView(map);
        mc.setMaximumScale(9f);
        mc.update();


        //create LocationHandler
        LocationHandler locationHandler = new LocationHandler(this, true); //TODO updatesEnabled aus config laden

        //create UserDataSync
        UserDataSynchronizer uds = new UserDataSynchronizer(this, locationHandler, map);
        uds.startUpdating();


    }
}
