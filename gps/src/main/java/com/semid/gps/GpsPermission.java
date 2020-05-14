package com.semid.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class GpsPermission {
    public static boolean checkLocation(Context context, boolean withBackground) {
        boolean fine = checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean coarse = checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fine && coarse) {

            boolean bgLocationIsAllow = true;

            if (withBackground && aboveAndroidQ())
                bgLocationIsAllow = checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            return bgLocationIsAllow;
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

    public static MutableLiveData<Boolean> requestLocation(Context context, boolean withBackground) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        if (!checkLocation(context, withBackground)) {
            ArrayList<String> list = new ArrayList<>();
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (aboveAndroidQ() && withBackground)
                list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            Dexter.withContext(context)
                    .withPermissions(list)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            if (multiplePermissionsReport.getDeniedPermissionResponses().size() == 1) {
                                if (multiplePermissionsReport.getDeniedPermissionResponses().get(0).getPermissionName().contains("ACCESS_BACKGROUND_LOCATION")) {
                                    liveData.postValue(true);
                                    return;
                                }
                            }

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

    public static boolean aboveAndroidQ() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    }

    private static boolean checkSelfPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
