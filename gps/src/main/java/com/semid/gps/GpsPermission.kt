package com.semid.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*

object GpsPermission {
    fun checkFullLocation(context: Context, withBackground: Boolean): Boolean {
        return checkLocation(context, withBackground) && isGpsEnabled(context)
    }

    @JvmStatic
    fun checkLocation(context: Context?, withBackground: Boolean): Boolean {
        context?.let {
            val fine = checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarse = checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            return if (fine && coarse) {
                var bgLocationIsAllow = true
                if (withBackground && aboveAndroidQ()) bgLocationIsAllow = checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                bgLocationIsAllow
            } else false
        }
        return false
    }

    @JvmStatic
    fun isGpsEnabled(context: Context?): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
    }

    @JvmStatic
    fun requestLocation(context: Context?, withBackground: Boolean): SingleLiveEvent<Boolean> {
        val liveData = SingleLiveEvent<Boolean>()
        if (!checkLocation(context, withBackground)) {

            val list = ArrayList<String>()
            list.add(Manifest.permission.ACCESS_FINE_LOCATION)
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION)

            if (aboveAndroidQ() && withBackground)
                list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

            Dexter.withContext(context)
                    .withPermissions(list)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                            if (multiplePermissionsReport.deniedPermissionResponses.size == 1) {
                                if (multiplePermissionsReport.deniedPermissionResponses[0].permissionName.contains("ACCESS_BACKGROUND_LOCATION")) {
                                    liveData.postValue(true)
                                    return
                                }
                            }
                            liveData.postValue(multiplePermissionsReport.areAllPermissionsGranted())
                        }

                        override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                            permissionToken.continuePermissionRequest()
                        }
                    }).check()
        } else liveData.postValue(true)
        return liveData
    }

    private fun aboveAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    private fun checkSelfPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}