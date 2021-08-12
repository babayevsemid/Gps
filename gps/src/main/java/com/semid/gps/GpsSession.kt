package com.semid.gps

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit

class GpsSession private constructor() {
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences("com.semid.gps", Context.MODE_PRIVATE)
    }

    var lastLocation: Location
        get() {
            val loc = Location("passive")
            loc.latitude = preferences.getString(KEY_LAST_LAT, "0")!!.toDouble()
            loc.longitude = preferences.getString(KEY_LAST_LNG, "0")!!.toDouble()
            return loc
        }
        set(location) {
            preferences.edit {
                putString(KEY_LAST_LAT, location.latitude.toString())
                putString(KEY_LAST_LNG, location.longitude.toString())
            }
        }

    companion object {
        @Volatile
        private var INSTANCE: GpsSession? = null
        private const val KEY_LAST_LAT = "last lat"
        private const val KEY_LAST_LNG = "last lng"

        fun getInstance(context: Context): GpsSession {
            return INSTANCE ?: synchronized(this) {

                val instance = GpsSession()
                instance.init(context)

                INSTANCE = instance
                instance
            }
        }
    }
}