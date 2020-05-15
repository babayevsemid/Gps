package com.semid.gpscallback;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.semid.gps.GpsManager;
import com.semid.gps.GpsPermission;

public class MainActivity extends AppCompatActivity {
    private TextView lastLocationTxt, newLocationTxt, gpsTxt;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();

        gpsIsEnabled();
    }

    private void findView() {
        lastLocationTxt = findViewById(R.id.lastLocationTxt);
        newLocationTxt = findViewById(R.id.newLocationTxt);
        gpsTxt = findViewById(R.id.gpsTxt);
        btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }


    private void getLocation() {
        GpsManager.LocationCallback callback = new GpsManager.LocationCallback() {
            @Override
            public void onNewLocationAvailable(double lat, double lon) {
                Log.e("onNewLocationAvailable",lat+","+lon);

                newLocationTxt.setText("New location : " + lat + "," + lon);
            }

            @Override
            public void onLastKnownLocation(double lat, double lon) {
                Log.e("onLastKnownLocation",lat+","+lon);

                lastLocationTxt.setText("Last known location : " + lat + "," + lon);
            }

            @Override
            public void onBackgroundNotAvailable() {
                Log.e("onBackgroundNotAv","onBackgroundNotAvailable");
            }

            @Override
            public void onNotAvailable() {
                Log.e("onNotAvailable","onNotAvailable");
            }
        };

        new GpsManager.Builder()
                .setActivity(this)
                .setDistance(1)
                .setUpdateTime(2000)
                .setListener(callback)
                .setOnResumeConnect(true)
                .setOnPauseDisconnect(true)
                .setTrackingEnabled(true)
                .setWithBackgroundPermission(true)
                .create();
    }

    @Override
    protected void onResume() {
        super.onResume();

        gpsIsEnabled();
    }

    private void gpsIsEnabled() {
        if (GpsPermission.isGpsEnabled(getApplicationContext()))
            gpsTxt.setText("Gps status : enabled");
        else
            gpsTxt.setText("Gps status : disabled");
    }
}
