package com.semid.gpscallback;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.semid.gps.GpsManager;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        getLocation();
    }
        private void getLocation() {
            GpsManager.LocationCallback callback = new GpsManager.LocationCallback() {
                @Override
                public void onNewLocationAvailable(double lat, double lon) {
                    Log.e("onNewLocationAvaTest",lat+","+lon);
                }

                @Override
                public void onLastKnownLocation(double lat, double lon) {
                    Log.e("onLastKnownLocTest",lat+","+lon);

                }

                @Override
                public void onBackgroundNotAvailable() {
                    Log.e("onBackgroundNTest","onBackgroundNotAvailable");
                }

                @Override
                public void onNotAvailable() {
                    Log.e("onNotAvaiTest","onNotAvailable");
                }
            };

            new GpsManager.Builder()
                    .setActivity(this)
                    .setDistance(1)
                    .setUpdateTime(2000)
                    .setListener(callback)
                    .setOnResumeConnect(false)
                    .setOnPauseDisconnect(false)
                    .setTrackingEnabled(false)
                    .setWithBackgroundPermission(false)
                    .create();
        }
}
