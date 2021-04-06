package com.semid.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

public class GpsConnectorReceiver extends BroadcastReceiver {
    private final Handler handler = new Handler();
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (intent.getAction() != null && intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 300);
        }
    }

    Runnable runnable = () -> {
        GpsManager.gpsEnableLiveData.postValue(GpsPermission.isGpsEnabled(context));
    };

}
