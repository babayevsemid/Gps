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
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class GpsConfiguration : LifecycleObserver, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var locationManager: LocationManager
    private lateinit var locationRequest: LocationRequest
    private var mGoogleApiClient: GoogleApiClient? = null

    private val gpsReceiver = GpsConnectorReceiver()

    private var session: GpsSession? = null
    private lateinit var builder: GpsBuilder

    private var isCanceledPermission = false


    fun build(build: GpsBuilder) {
        builder = build

        session = builder.context?.let { GpsSession.getInstance(it) }

        isCanceledPermission = false

        if (builder.activity == null) {
            disconnect()
        }

        init()

        builder.lifecycle?.let {
            builder.lifecycle?.removeObserver(this)
            builder.lifecycle?.addObserver(this)

            GpsManager.gpsEnableLiveData.observeForever {
                if (it && GpsPermission.checkLocation(builder.context, withBackground = false))
                    initGpsTracking()
            }
        }

        requestPermission()
        initLastKnownLocation()
    }

    private fun init() {
        builder.context?.let {
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mSettingsClient = LocationServices.getSettingsClient(it)
            locationRequest = LocationRequest.create()
            locationRequest.priority = builder.priority.value

            if (builder.trackingEnabled) {
                locationRequest.smallestDisplacement = builder.distance.toFloat()
                locationRequest.interval = builder.updateTime
                locationRequest.fastestInterval = builder.updateTime / 2
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            mLocationSettingsRequest = builder.build()
        }
    }

    private fun requestPermission() {
        builder.lifecycleOwner?.let { owner ->
            GpsPermission.requestLocation(builder.context, builder.withBackgroundPermission)
                .observe(owner) {
                    if (GpsPermission.checkLocation(builder.context, withBackground = false)) {
                        if (GpsPermission.isGpsEnabled(builder.context)) {
                            initGpsTracking()
                        } else {
                            turnGPSOn()
                        }
                    } else {
                        isCanceledPermission = true

                        checkPermission()
                    }
                }
        }
    }

    private fun checkPermission() {
        if (!GpsPermission.checkLocation(builder.context, false))
            builder.onNotAvailable?.invoke()

        if (!GpsPermission.checkLocation(builder.context, true))
            builder.onBackgroundNotAvailable?.invoke()
    }

    private fun turnGPSOn() {
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
                        } catch (ignored: IntentSender.SendIntentException) {
                        }
                    }
                }
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
                if (session?.lastLocation?.latitude != 0.0)
                    return session?.lastLocation

                if (GpsPermission.checkLocation(it, false)) {
                    val list = listOf("gps", "passive", "network")

                    for (provider in list) {
                        val location = locationManager.getLastKnownLocation(provider)

                        if (location != null) {
                            session?.lastLocation = location
                            return location
                        }
                    }
                }

                if (builder.defaultLocation?.latitude != 0.0)
                    return builder.defaultLocation else null
            }

            return null
        }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        try {
            GpsManager.isConnected = true

            if (GpsPermission.checkLocation(builder.context, false))
                mGoogleApiClient?.let {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                        it,
                        locationRequest,
                        this
                    )
                }
        } catch (ignored: Exception) {
        }
    }

    override fun onConnectionSuspended(i: Int) {
        GpsManager.isConnected = false
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        GpsManager.isConnected = false
    }

    override fun onLocationChanged(location: Location) {
        GpsManager.location.value = location

        session?.lastLocation = location

        builder.onNewLocationAvailable?.invoke(location.latitude, location.longitude)

        if (!builder.trackingEnabled)
            disconnect()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (builder.onResumeConnect &&
            GpsPermission.checkFullLocation(builder.context!!, withBackground = false)
        ) {
            disconnect()
            Log.e("onResume",builder.onResumeConnect.toString())

            initGpsTracking()
        }

        try {
            builder.activity?.registerReceiver(
                gpsReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        } catch (ignored: Exception) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (builder.onPauseDisconnect) {
            disconnect()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        try {
            builder.activity?.unregisterReceiver(gpsReceiver)
        } catch (ignored: Exception) {
        }
    }

    fun disconnect() {
        mGoogleApiClient?.disconnect()

        GpsManager.isConnected = false
    }

    @SuppressLint("MissingPermission")
    private fun initGpsTracking() {
        builder.context?.let {
            if (!GpsPermission.checkLocation(it, false)) return

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