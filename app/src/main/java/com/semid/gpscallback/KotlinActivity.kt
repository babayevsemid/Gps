package com.semid.gpscallback

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.semid.gps.GpsBuilder
import com.semid.gps.GpsManager
import com.semid.gps.GpsPermission.isGpsEnabled
import com.semid.gpscallback.databinding.ActivityKotlinBinding

class KotlinActivity : AppCompatActivity() {
    private val binding: ActivityKotlinBinding by lazy {
        ActivityKotlinBinding.inflate(layoutInflater)
    }

    private lateinit var manager: GpsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            getLocation()
        }

        initLocation()
        checkGpsEnableLiveData()
    }

    private fun initLocation() {
        val builder = GpsBuilder(this)
                .configDistance(1)
                .configUpdateTime(2000)
                .configTrackingEnabled(true)
                .configOnResumeConnect(true)
                .configOnPauseDisconnect(false)
                .configDefaultLocation(42.235476235, 41.236453265)

        builder.onNewLocationAvailable = { lat: Double, lon: Double ->
            Log.e("onNewLocationAvailable", "$lat,$lon")
            binding.newLocationTxt.text = "New location : $lat,$lon"
        }

        builder.onLastKnownLocation = { lat: Double, lon: Double ->
            Log.e("onLastKnownLocation", "$lat,$lon")
            binding.lastLocationTxt.text = "Last known location : $lat,$lon"
        }

        builder.onBackgroundNotAvailable = {
            Log.e("onBackgroundNotAv", "onBackgroundNotAvailable")
        }

        builder.onNotAvailable = {
            Log.e("onNotAvailable", "onNotAvailable")
        }

        manager = GpsManager(builder)
    }

    private fun getLocation() {
        manager.connect()
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