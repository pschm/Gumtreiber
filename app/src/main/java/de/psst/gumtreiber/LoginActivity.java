package de.psst.gumtreiber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MapView map = findViewById(R.id.map);
        map.setUserList(User.getDummyData());

        // enable zoom effect
        MapControl mc = new MapControl(map, true);
        mc.setMapView(map);
        mc.setMaximumScale(9f);
        mc.update();
    }
}
