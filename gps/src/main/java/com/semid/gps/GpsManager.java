package com.semid.gps;

import android.content.Context;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

public class GpsManager {
	public static Location location;

	public GpsManager() {

	}

    public static void setLocation(Location location) {
        GpsManager.location = location;
    }

    public static class Builder {
		LocationCallback callback;
		AppCompatActivity activity;
		Context context;
		int distance = 10;
		int updateTime = 2000;
		boolean trackingEnabled;
		boolean onResumeConnect = true;
		boolean onPauseDisconnect = true;
		boolean withBackgroundPermission = false;

		public Builder() {
		}

		public Builder setContext(Context context) {
			this.context = context;
			return this;
		}

		public Builder setActivity(AppCompatActivity activity) {
			this.activity = activity;
			this.context = activity.getApplicationContext();

			return this;
		}

		public Builder setListener(LocationCallback callback) {
			this.callback = callback;
			return this;
		}

		public Builder setUpdateTime(int updateTime) {
			this.updateTime = updateTime;
			return this;
		}

		public Builder setOnResumeConnect(boolean onResumeConnect) {
			this.onResumeConnect = onResumeConnect;
			return this;
		}

		public Builder setWithBackgroundPermission(boolean withBackgroundPermission) {
			this.withBackgroundPermission = withBackgroundPermission;
			return this;
		}


		public Builder setOnPauseDisconnect(boolean onPauseDisconnect) {
			this.onPauseDisconnect = onPauseDisconnect;
			return this;
		}

		public Builder setDistance(int distanceMeter) {
			this.distance = distanceMeter;
			return this;
		}

		public Builder setTrackingEnabled(boolean trackingEnabled) {
			this.trackingEnabled = trackingEnabled;
			return this;
		}

		public GpsConfiguration create() {
			if (this.context == null) {
				try {
					throw new Exception("Context needs to be passed in");
				} catch (Exception var3) {
					var3.printStackTrace();
				}
			}

			if (this.callback == null) {
				try {
					throw new Exception("No callback provided, do you expect updates?");
				} catch (Exception var2) {
					var2.printStackTrace();
				}
			}

			GpsConfiguration configuration = GpsConfiguration.getInstance(activity)
					.setBuilder(Builder.this);

			return configuration;
		}
	}

	public static abstract class LocationCallback {
		public void onNewLocationAvailable(double lat, double lon) {

		}

		public void onLastKnownLocation(double lat, double lon) {

		}

		public void onBackgroundNotAvailable() {

		}

		public void onNotAvailable() {

		}
	}
}
