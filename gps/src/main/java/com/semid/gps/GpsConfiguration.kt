@file:Suppress("DEPRECATION")

package com.semid.gps

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.semid.gps.GpsPermission.checkLocation
import com.semid.gps.GpsPermission.isGpsEnabled
import com.semid.gps.GpsPermission.requestLocation

class GpsConfiguration : LifecycleObserver, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var locationManager: LocationManager
    private lateinit var locationRequest: LocationRequest
    private var mGoogleApiClient: GoogleApiClient? = null

    private val gpsReceiver = GpsConnectorReceiver()

    private var session: GpsSession? = null
    private lateinit var builder: GpsBuilder

    private var isCanceledPermission = false
    private var bgRequestCanceled = false
    private var requestedSettingPermission = false


    fun build(build: GpsBuilder) {
        builder = build

        session = builder.context?.let { GpsSession(it) }


        isCanceledPermission = false
        bgRequestCanceled = false
        requestedSettingPermission = false

        init()

        if (builder.activity == null) {
            disConnect()
            connect()
        }

        builder.lifecycleOwner?.let {
            builder.lifecycle?.addObserver(this)

            GpsManager.gpsEnableLiveData.observe(it, { aBoolean: Boolean ->
                if (aBoolean && checkLocation(builder.context, false)) {
                    requestedSettingPermission = true
                    initGpsTracking()
                }
            })
        }
    }

    private fun init() {
        builder.context?.let {
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mSettingsClient = LocationServices.getSettingsClient(it)
            locationRequest = LocationRequest.create()
            locationRequest.smallestDisplacement = builder.distance.toFloat()
            locationRequest.interval = builder.updateTime
            locationRequest.fastestInterval = builder.updateTime / 2
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)

            mLocationSettingsRequest = builder.build()
            builder.setAlwaysShow(true)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connect() {
        builder.context?.let {
            if (!builder.onResumeConnect && requestedSettingPermission) return

            if (!isCanceledPermission) {
                if (checkLocation(builder.context, builder.withBackgroundPermission) || bgRequestCanceled) {
                    turnGPSOn()
                } else if (!requestedSettingPermission) {
                    requestLocation(builder.context, builder.withBackgroundPermission)
                            .observeForever { aBoolean: Boolean ->
                                if (aBoolean) {
                                    turnGPSOn()

                                    if (!checkLocation(builder.context, true)) {
                                        bgRequestCanceled = true
                                        builder.onBackgroundNotAvailable?.invoke()
                                    }
                                } else {
                                    isCanceledPermission = true
                                    checkPermission()
                                    initLastKnownLocation()
                                }
                            }
                } else {
                    checkPermission()
                }
            }
        }
    }

    private fun checkPermission() {
        if (!checkLocation(builder.context, false))
            builder.onNotAvailable?.invoke()
        else
            builder.onBackgroundNotAvailable?.invoke()
    }

    private fun turnGPSOn() {
        if (builder.context == null && requestedSettingPermission)
            return

        if (isGpsEnabled(builder.context)) {
            if (!requestedSettingPermission)
                initLastKnownLocation()

            initGpsTracking()
        } else if (builder.activity != null && !requestedSettingPermission) {
            initLastKnownLocation()

            builder.activity?.let {
                mSettingsClient
                        .checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener(it) {
                            Handler(Looper.getMainLooper())
                                    .postDelayed({ initGpsTracking() }, 300)
                        }
                        .addOnFailureListener(it) { e ->
                            val statusCode = (e as ApiException).statusCode
                            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                                try {
                                    val rae = e as ResolvableApiException
                                    rae.startResolutionForResult(it, 111)
                                    requestedSettingPermission = true
                                } catch (ignored: IntentSender.SendIntentException) {
                                }
                            }
                        }
            }
        } else {
            builder.onNotAvailable?.invoke()
        }
    }

    private fun initLastKnownLocation() {
        val passive = lastKnownLocation

        if (passive != null) {
            GpsManager.location.value = passive
            builder.onLastKnownLocation?.invoke(passive.latitude, passive.longitude)
        }
    }

    @get:SuppressLint("MissingPermission")
    val lastKnownLocation: Location?
        get() {
            builder.context?.let {
                return if (checkLocation(it, false)) {
                    val list = listOf("gps", "passive", "network")

                    for (provider in list) {
                        val location = locationManager.getLastKnownLocation(provider)

                        if (location != null) {
                            session!!.lastLocation = location
                            return location
                        }
                    }
                    if (session!!.lastLocation.latitude != 0.0)
                        return session!!.lastLocation

                    if (builder.defaultLocation?.latitude != 0.0)
                        builder.defaultLocation else null

                } else {
                    if (session!!.lastLocation.latitude != 0.0)
                        return session!!.lastLocation

                    if (builder.defaultLocation?.latitude != 0.0)
                        builder.defaultLocation else null
                }
            }

            return null
        }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        try {
            if (checkLocation(builder.context, false))
                mGoogleApiClient?.let {
                    LocationServices.FusedLocationApi.requestLocationUpdates(it, locationRequest, this)
                }
        } catch (ignored: Exception) {
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onLocationChanged(location: Location) {
        GpsManager.location.value = location

        session!!.lastLocation = location

        builder.onNewLocationAvailable?.invoke(location.latitude, location.longitude)

        if (!builder.trackingEnabled)
            disConnect()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        try {
            builder.activity?.registerReceiver(gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        } catch (ignored: Exception) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disConnect() {
        if (builder.onPauseDisconnect) {
            if (mGoogleApiClient?.isConnected == true) {
                mGoogleApiClient?.disconnect()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        try {
            builder.activity?.unregisterReceiver(gpsReceiver)
        } catch (ignored: Exception) {
        }
    }

    private fun initGpsTracking() {
        builder.context?.let {
            if (!checkLocation(it, false)) return

            if (mGoogleApiClient == null)
                mGoogleApiClient = GoogleApiClient.Builder(it)
                        .addOnConnectionFailedListener(this)
                        .addConnectionCallbacks(this)
                        .addApi(LocationServices.API)
                        .build()

            if (mGoogleApiClient?.isConnected == false)
                mGoogleApiClient?.connect()
        }
    }
}