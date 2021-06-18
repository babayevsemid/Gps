package com.semid.gps

import androidx.annotation.IntDef
import com.google.android.gms.location.LocationRequest

enum class LocationRequestPriority(val value: Int) {
    PRIORITY_HIGH_ACCURACY(LocationRequest.PRIORITY_HIGH_ACCURACY),
    PRIORITY_BALANCED_POWER_ACCURACY(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
    PRIORITY_LOW_POWER(LocationRequest.PRIORITY_LOW_POWER),
    PRIORITY_NO_POWER(LocationRequest.PRIORITY_NO_POWER)
}