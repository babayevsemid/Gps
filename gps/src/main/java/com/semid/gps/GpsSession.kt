package com.semid.gps

import android.content.Context
import android.content.SharedPreferences
import android.location.Location

class GpsSession(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("com.semid.gps", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor? = null

    var lastLocation: Location
        get() {
            val loc = Location("passive")
            loc.latitude = preferences.getString(KEY_LAST_LAT, "0")!!.toDouble()
            loc.longitude = preferences.getString(KEY_LAST_LNG, "0")!!.toDouble()
            return loc
        }
        set(location) {
            editor = preferences.edit()
            editor?.putString(KEY_LAST_LAT, location.latitude.toString())
            editor?.putString(KEY_LAST_LNG, location.longitude.toString())
            editor?.apply()
        }

    companion object {
        private const val KEY_LAST_LAT = "last lat"
        private const val KEY_LAST_LNG = "last lng"
    }
}