package com.semid.gpscallback;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.semid.gps.GpsBuilder;
import com.semid.gps.GpsManager;
import com.semid.gps.GpsPermission;
import com.semid.gpscallback.databinding.ActivityMainBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private GpsManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btn.setOnClickListener(view -> manager.connect());

        initLocation();
        checkGpsEnableLiveData();
    }

    private void initLocation() {
        manager = new GpsBuilder(this)
                .configDistance(1)
                .configUpdateTime(2000)
                .configTrackingEnabled(true)
                .configOnResumeConnect(true)
                .configOnPauseDisconnect(false)
                .configDefaultLocation(42.235476235, 41.236453265)
                .build();

        manager.onNewLocationAvailable = (lat, lon) -> {
            Log.e("onNewLocationAvailable", lat + ", " + lon);

            binding.newLocationTxt.setText("New location : " + lat + ", " + lon);
            return null;
        };

        manager.onLastKnownLocation = (lat, lon) -> {
            Log.e("onNewLocationAvailable", lat + ", " + lon);

            binding.lastLocationTxt.setText("Last known location : " + lat + ", " + lon);
            return null;
        };


        manager.onBackgroundNotAvailable = () -> {
            Log.e("onBackgroundNotAv", "onBackgroundNotAvailable");

            return null;
        };

        manager.onNotAvailable = () -> {
            Log.e("onNotAvailable", "onNotAvailable");

            return null;
        };
    }

    private void checkGpsEnableLiveData() {
        GpsManager.gpsEnableLiveData.observeForever(aBoolean -> {
            Log.e("gpsEnableLiveData", aBoolean + "");

            if (aBoolean)
                binding.gpsTxt.setText("Gps status : enabled");
            else
                binding.gpsTxt.setText("Gps status : disabled");
        });
        GpsManager.gpsEnableLiveData.postValue(GpsPermission.isGpsEnabled(getApplicationContext()));
    }
}
