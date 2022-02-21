package com.ryanamaral.arykey.common.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


fun Context.hasPermissions(vararg permissions: String): Boolean {
    return permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.shouldShowPermissionRationale(vararg permissions: String): Boolean {
    return permissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(this, it)
    }
}
