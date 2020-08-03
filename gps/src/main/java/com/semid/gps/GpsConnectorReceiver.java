package com.semid.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GpsConnectorReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction() != null && intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
			GpsManager.gpsEnableLiveData.postValue(GpsPermission.isGpsEnabled(context));
		}
	}
}
