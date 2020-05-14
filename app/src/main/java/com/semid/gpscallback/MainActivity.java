package com.semid.gpscallback;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.semid.gps.GpsManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    private void init() {
        GpsManager.LocationCallback callback = new GpsManager.LocationCallback() {
            @Override
            public void onNewLocationAvailable(double lat, double lon) {
                Log.e("lat,lon", lat + "");
            }

            @Override
            public void onLastKnownLocation(double lat, double lon) {
                Log.e("onLastKnownLocation", lat +"");
            }

            @Override
            public void onNotAvailable() {
                Log.e("onNotAvailable", "onNotAvailable");
            }
        };

        new GpsManager.Builder()
                .setActivity(this)
                .setDistance(1)
                .setListener(callback)
                .setOnResumeConnect(true)
                .setTrackingEnabled(false)
                .setUpdateTime(2000)
                .create();
    }
}
