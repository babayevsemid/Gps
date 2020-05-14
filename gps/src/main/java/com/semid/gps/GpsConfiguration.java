package com.semid.gps;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import java.util.Arrays;
import java.util.List;

public class GpsConfiguration implements LifecycleObserver, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static GpsConfiguration instance;

    private GpsManager.Builder builder;

    private boolean canceledPermission;
    private boolean requestedSettingPermission;

    public GpsConfiguration() {

    }

    public static GpsConfiguration getInstance() {
        if (instance == null)
            instance = new GpsConfiguration();

        return instance;
    }

    public GpsConfiguration(GpsManager.Builder builder) {
        this.builder = builder;

        init();

        if (builder.activity != null)
            builder.activity.getLifecycle().addObserver(this);
        else
            connect();
    }

    private void init() {
        locationManager = (LocationManager) builder.context.getSystemService(Context.LOCATION_SERVICE);
        mSettingsClient = LocationServices.getSettingsClient(builder.context);
        locationRequest = LocationRequest.create();

        locationRequest.setSmallestDisplacement(builder.distance);
        locationRequest.setInterval(builder.updateTime);
        locationRequest.setFastestInterval(builder.updateTime);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        mLocationSettingsRequest = builder.build();

        builder.setAlwaysShow(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void connect() {
        if (builder.context == null || !builder.onResumeConnect)
            return;

        if (!isCanceledPermission())
            if (checkPermissions()) {
                turnGPSOn();
            } else {
                GpsPermission.requestLocation(builder.context)
                        .observeForever(aBoolean -> {
                            if (aBoolean)
                                turnGPSOn();
                            else {
                                canceledPermission = true;

                                builder.callback.onNotAvailable();
                            }
                        });
            }
    }

    private void turnGPSOn() {
        if (builder.context == null)
            return;

        Location passive = getLastKnownLocation();
        if (passive != null)
            builder.callback.onLastKnownLocation(passive.getLatitude(), passive.getLongitude());

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            initGpsTracking();
        } else if (builder.activity != null && !requestedSettingPermission) {
            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(builder.activity, locationSettingsResponse -> {
                        initGpsTracking();
                    })
                    .addOnFailureListener(builder.activity, e -> {
                        int statusCode = ((ApiException) e).getStatusCode();
                        if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(builder.activity, 111);

                                requestedSettingPermission = true;
                            } catch (IntentSender.SendIntentException ignored) {
                            }
                        } else {
                            canceledPermission = true;
                        }
                    });
        } else {
            builder.callback.onNotAvailable();
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLastKnownLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) builder.context.getSystemService(Context.LOCATION_SERVICE);

        if (GpsPermission.checkLocation(builder.context)) {
            List<String> list = Arrays.asList("gps", "passive", "network");

            for (String provider : list) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null)
                    return location;
            }

            return null;
        } else
            return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            builder.callback.onNewLocationAvailable(location.getLatitude(), location.getLongitude());

        if (!builder.trackingEnabled)
            disConnect();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disConnect() {
        if (builder.onPauseDisconnect)
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
    }

    private void initGpsTracking() {
        if (!checkPermissions())
            return;

        mGoogleApiClient = new GoogleApiClient.Builder(builder.context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    public boolean isCanceledPermission() {
        return canceledPermission;
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(builder.context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}
