package com.semid.gpscallback

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.semid.gps.GpsBuilder
import com.semid.gps.GpsManager
import com.semid.gps.GpsPermission.isGpsEnabled
import com.semid.gps.LocationRequestPriority
import com.semid.gpscallback.databinding.ActivityKotlinBinding

class KotlinActivity : AppCompatActivity() {
    private val binding: ActivityKotlinBinding by lazy {
        ActivityKotlinBinding.inflate(layoutInflater)
    }

    private var manager: GpsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            getLocation()
        }

        binding.root.setOnClickListener {
            Log.e("disss","clikck")
            manager?.disconnect()
        }

        initLocation()
        checkGpsEnableLiveData()
    }

    private fun initLocation() {
        manager = GpsBuilder(this)
            .configOnResumeConnect(true)
            .configOnPauseDisconnect(false)
            .configDefaultLocation(42.235476235, 41.236453265)
            .configPriority(LocationRequestPriority.PRIORITY_HIGH_ACCURACY)
            .build()

        manager?.onNewLocationAvailable = { lat: Double, lon: Double ->
            Log.e("onNewLocationAvailable", "$lat,$lon")
            binding.newLocationTxt.text = "New location : $lat,$lon"
        }

        manager?.onLastKnownLocation = { lat: Double, lon: Double ->
            Log.e("onLastKnownLocation", "$lat,$lon")
            binding.lastLocationTxt.text = "Last known location : $lat,$lon"
        }

        manager?.onBackgroundNotAvailable = {
            Log.e("onBackgroundNotAv", "onBackgroundNotAvailable")
        }

        manager?.onNotAvailable = {
            Log.e("onNotAvailable", "onNotAvailable")
        }
    }

    private fun getLocation() {
        manager?.connect()
    }

    private fun checkGpsEnableLiveData() {
        GpsManager.gpsEnableLiveData.observeForever { enabled: Boolean ->
            Log.e("gpsEnableLiveData", enabled.toString() + "")

            if (enabled)
                binding.gpsTxt.text = "Gps status : enabled"
            else
                binding.gpsTxt.text = "Gps status : disabled"
        }
        GpsManager.gpsEnableLiveData.postValue(isGpsEnabled(applicationContext))
    }
}