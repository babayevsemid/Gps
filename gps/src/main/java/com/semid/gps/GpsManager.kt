package com.semid.gps

import android.content.Context
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

        @JvmStatic
        fun session(context: Context) = GpsSession.getInstance(context)

        @JvmField
        var isConnected = false

    }

    init {
        builder.onNewLocationAvailable =
            { lat: Double, lng: Double -> onNewLocationAvailable?.invoke(lat, lng) }
        builder.onLastKnownLocation =
            { lat: Double, lng: Double -> onLastKnownLocation?.invoke(lat, lng) }
        builder.onNotAvailable = { onNotAvailable?.invoke() }
        builder.onBackgroundNotAvailable = { onBackgroundNotAvailable?.invoke() }
    }

    @JvmField
    var onNewLocationAvailable: ((lat: Double, lng: Double) -> Unit)? = null

    @JvmField
    var onLastKnownLocation: ((lat: Double, lng: Double) -> Unit)? = null

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