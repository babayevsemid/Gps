package com.semid.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.CountDownTimer
import com.semid.gps.GpsPermission.isGpsEnabled

class GpsConnectorReceiver : BroadcastReceiver() {
    private var context: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        if (intent.action != null && intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            countDownTimer.cancel()
            countDownTimer.start()
        }
    }

    private val countDownTimer = object : CountDownTimer(700, 100) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            GpsManager.gpsEnableLiveData.postValue(isGpsEnabled(context!!))
        }
    }
}