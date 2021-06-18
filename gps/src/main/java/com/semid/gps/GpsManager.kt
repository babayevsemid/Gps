package com.semid.gps

import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData

class GpsManager(builder: GpsBuilder) {
    private var gpsConfiguration: GpsConfiguration? = null

    fun connect() {
        countDownTimer.cancel()
        countDownTimer.start()
    }

    fun disconnect() {
        countDownTimer.cancel()

        gpsConfiguration?.disconnect()
    }

    companion object {
        @JvmField
        var gpsEnableLiveData = MutableLiveData<Boolean>()

        @JvmField
        var location = MutableLiveData<Location>()

        @JvmField
        var isConnected = false

    }

    init {
        builder.onNewLocationAvailable =
            { lat: Double, lon: Double -> onNewLocationAvailable?.invoke(lat, lon) }
        builder.onLastKnownLocation =
            { lat: Double, lon: Double -> onLastKnownLocation?.invoke(lat, lon) }
        builder.onNotAvailable = { onNotAvailable?.invoke() }
        builder.onBackgroundNotAvailable = { onBackgroundNotAvailable?.invoke() }
    }

    @JvmField
    var onNewLocationAvailable: ((lat: Double, lon: Double) -> Unit)? = null

    @JvmField
    var onLastKnownLocation: ((lat: Double, lon: Double) -> Unit)? = null

    @JvmField
    var onNotAvailable: (() -> Unit)? = null

    @JvmField
    var onBackgroundNotAvailable: (() -> Unit)? = null


    private val countDownTimer = object : CountDownTimer(300, 100) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            gpsConfiguration = GpsConfiguration().build(builder)
        }
    }
}