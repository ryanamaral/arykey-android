package com.ryanamaral.arykey.module.packagename

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.ryanamaral.arykey.BuildConfig
import timber.log.Timber


/**
 * Enable Accessibility Services of this app via adb
 * $ adb shell settings put secure enabled_accessibility_services com.ryanamaral.arykey/.features.packagename.PackageAccessibilityService
 *
 * Accessibility Settings: `startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))`
 *
 * Docs: https://developer.android.com/guide/topics/ui/accessibility/service
 */
class PackageAccessibilityService : AccessibilityService() {

    public override fun onServiceConnected() {
        super.onServiceConnected()

        serviceInfo = AccessibilityServiceInfo().apply {
            flags = AccessibilityServiceInfo.DEFAULT
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        }
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        if (accessibilityEvent.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            try {
                val currentPackageName = accessibilityEvent.source.packageName.toString()
                if (currentPackageName == applicationContext.packageName) return
                if (shouldSendThisPackageName(
                        packageName = currentPackageName,
                        className = accessibilityEvent.className.toString()
                    )
                ) {
                    sendBroadcast(Intent(ACTION_FOREGROUND_APPLICATION_CHANGED).apply {
                        putExtra(EXTRA_PACKAGE_NAME, currentPackageName)
                        putExtra(EXTRA_CLASS_NAME, accessibilityEvent.className.toString())
                    }, ACCESSIBILITY_SERVICE_PERMISSION)
                    Timber.d("PackageName: $currentPackageName")
                }
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                try {
                    accessibilityEvent.source.recycle()
                } catch (ignore: Exception) {
                    // do nothing
                }
            }
        }
    }

    override fun onInterrupt() {
        // do nothing
    }

    private fun shouldSendThisPackageName(packageName: String, className: String): Boolean =
        (packageName == applicationContext.packageName
                || packageName == "com.android.systemui"
                || packageName.lowercase().contains("launcher")
                || className.lowercase().contains("launcher")).not()

    companion object {
        const val ACTION_FOREGROUND_APPLICATION_CHANGED =
            "${BuildConfig.APPLICATION_ID}.ACTION_FOREGROUND_APPLICATION_CHANGED"
        const val EXTRA_PACKAGE_NAME =
            "${BuildConfig.APPLICATION_ID}.EXTRA_PACKAGE_NAME"
        const val EXTRA_CLASS_NAME =
            "${BuildConfig.APPLICATION_ID}.EXTRA_CLASS_NAME"
        private const val ACCESSIBILITY_SERVICE_PERMISSION =
            "${BuildConfig.APPLICATION_ID}.ACCESSIBILITY_SERVICE_PERMISSION"
    }
}
