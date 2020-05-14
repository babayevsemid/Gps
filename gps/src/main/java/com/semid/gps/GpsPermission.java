package com.semid.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class GpsPermission {

    public static boolean checkBackgLocation(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int bgLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            return checkLocation(context) && bgLocation == PackageManager.PERMISSION_GRANTED;
        } else {
            return checkLocation(context);
        }
    }

    public static boolean checkLocation(Context context) {
        boolean permissionAccessFINE = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permissionAccessCOARSE = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFINE && permissionAccessCOARSE) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null) {
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } else
                return false;

        } else
            return false;
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null)
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        else
            return false;
    }

    public static MutableLiveData<Boolean> requestLocation(Context context) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        if (!checkLocation(context)) {
            ArrayList<String> list = new ArrayList<>();
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            Dexter.withContext(context)
                    .withPermissions(list)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            liveData.postValue(multiplePermissionsReport.areAllPermissionsGranted());
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        } else
            liveData.postValue(true);

        return liveData;
    }
}
