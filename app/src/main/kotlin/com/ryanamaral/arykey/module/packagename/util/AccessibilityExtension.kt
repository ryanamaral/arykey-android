package com.ryanamaral.arykey.module.packagename.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import timber.log.Timber


fun Context.isAccessibilityServiceEnabled(): Boolean {
    try {
        (getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager)?.let {
            for (service in it.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)) {
                if (service.id.contains(packageName)) return true
            }
        }
    } catch (e: Exception) {
        Timber.e(e)
    }
    return false
}
