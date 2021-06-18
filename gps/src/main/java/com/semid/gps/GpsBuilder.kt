package com.semid.gps

import android.content.Context
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class GpsBuilder {
    var defaultLocation: Location? = null
    var activity: AppCompatActivity? = null
    var context: Context? = null
    var lifecycle: Lifecycle? = null
    var lifecycleOwner: LifecycleOwner? = null

    var distance = 10
    var updateTime = 2000L
    var trackingEnabled = false
    var onResumeConnect = false
    var onPauseDisconnect = true
    var withBackgroundPermission = false

    var priority = LocationRequestPriority.PRIORITY_HIGH_ACCURACY

    init {
        defaultLocation = Location("passive")
        defaultLocation!!.latitude = 0.0
        defaultLocation!!.longitude = 0.0
    }

    constructor(context: Context) {
        this.context = context
    }

    constructor(activity: AppCompatActivity) {
        this.context = activity.applicationContext
        this.lifecycle = activity.lifecycle
        this.lifecycleOwner = activity
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        this.context = fragment.context
        this.lifecycle = fragment.lifecycle
        this.lifecycleOwner = fragment.viewLifecycleOwner
        this.activity = fragment.activity as AppCompatActivity?
    }

    fun configUpdateTime(updateTime: Long): GpsBuilder {
        this.updateTime = updateTime
        return this
    }

    fun configDefaultLocation(lat: Double, lng: Double): GpsBuilder {
        defaultLocation!!.latitude = lat
        defaultLocation!!.longitude = lng
        return this
    }

    fun configOnResumeConnect(onResumeConnect: Boolean): GpsBuilder {
        this.onResumeConnect = onResumeConnect
        return this
    }

    fun configWithBackgroundPermission(withBackgroundPermission: Boolean): GpsBuilder {
        this.withBackgroundPermission = withBackgroundPermission
        return this
    }

    fun configOnPauseDisconnect(onPauseDisconnect: Boolean): GpsBuilder {
        this.onPauseDisconnect = onPauseDisconnect
        return this
    }

    fun configDistance(distanceMeter: Int): GpsBuilder {
        distance = distanceMeter
        return this
    }

    fun configTrackingEnabled(trackingEnabled: Boolean): GpsBuilder {
        this.trackingEnabled = trackingEnabled
        return this
    }

    fun configPriority(priority: LocationRequestPriority): GpsBuilder {
        this.priority = priority
        return this
    }


    fun build(): GpsManager {
        return GpsManager(this)
    }

    @JvmField
    var onNewLocationAvailable: ((lat: Double, lon: Double) -> Unit)? = null

    @JvmField
    var onLastKnownLocation: ((lat: Double, lon: Double) -> Unit)? = null

    @JvmField
    var onNotAvailable: (() -> Unit)? = null

    @JvmField
    var onBackgroundNotAvailable: (() -> Unit)? = null
}