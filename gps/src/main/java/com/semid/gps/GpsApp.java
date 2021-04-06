package com.semid.gps;

import android.app.Application;
import android.content.IntentFilter;
import android.location.LocationManager;

public class GpsApp extends Application {
    private GpsConnectorReceiver gpsReceiver = new  GpsConnectorReceiver();

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterReceiver(gpsReceiver);
    }
}
