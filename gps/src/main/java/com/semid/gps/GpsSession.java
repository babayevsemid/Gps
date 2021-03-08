package com.semid.gps;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class GpsSession {
	private String KEY_LAST_LAT = "last lat";
	private String KEY_LAST_LNG = "last lng";

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	public GpsSession(Context context) {
		preferences = context.getSharedPreferences("com.semid.gps", Context.MODE_PRIVATE);
	}

	public Location getLastLocation() {
		Location loc = new Location("passive");
		loc.setLatitude(Double.parseDouble(preferences.getString(KEY_LAST_LAT, "0")));
		loc.setLongitude(Double.parseDouble(preferences.getString(KEY_LAST_LNG, "0")));
		return loc;
	}

	public void setLastLocation(Location location) {
		editor = preferences.edit();
		editor.putString(KEY_LAST_LAT, String.valueOf(location.getLatitude()));
		editor.putString(KEY_LAST_LNG, String.valueOf(location.getLongitude()));
		editor.apply();
	}
}
