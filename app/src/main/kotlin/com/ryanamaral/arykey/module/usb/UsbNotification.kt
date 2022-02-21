package com.ryanamaral.arykey.module.usb

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ryanamaral.arykey.MainActivity
import com.ryanamaral.arykey.R
import com.ryanamaral.arykey.module.usb.UsbService.Companion.CANCEL_USB_STATE_NOTIFICATION
import com.ryanamaral.arykey.module.usb.UsbService.Companion.NOTIFICATION_CHANNEL_ID


fun Context.buildUsbNotification(
    notificationManager: NotificationManager,
    usbAction: UsbAction?
): Notification {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // create notification channel for API 26+
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_title),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
    val mainNotificationText = getNotificationText(usbAction)
    val bigTextStyle = NotificationCompat.BigTextStyle()
        .bigText(mainNotificationText)
        .setBigContentTitle(getString(R.string.notification_title))

    // setup pending intents
    val servicePendingIntent = PendingIntent.getService(
        this,
        0,
        Intent(this, UsbService::class.java).apply {
            putExtra(CANCEL_USB_STATE_NOTIFICATION, true)
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val activityPendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or 0
    )

    // notification channel id is ignored for API <= 26
    return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        .setStyle(bigTextStyle)
        .setContentTitle(getString(R.string.notification_title))
        .setContentText(mainNotificationText)
        .setSmallIcon(R.drawable.ic_stat_app)
        .setDefaults(NotificationCompat.DEFAULT_SOUND)
        .setOngoing(true)
        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        .addAction(
            R.drawable.ic_open_in_app,
            getString(R.string.notification_action_launch),
            activityPendingIntent
        )
        .addAction(
            R.drawable.ic_close_octagon,
            getString(R.string.notification_action_stop),
            servicePendingIntent
        )
        .build()
}

fun Context.getNotificationText(usbAction: UsbAction?) = when (usbAction) {
    UsbAction.PermissionGranted -> getString(R.string.app_is_minimised) + getString(R.string.usb_permission_granted)
    UsbAction.PermissionNotGranted -> getString(R.string.app_is_minimised) + getString(R.string.usb_permission_not_granted)
    UsbAction.DeviceAttached -> getString(R.string.app_is_minimised) + getString(R.string.usb_device_attached)
    UsbAction.DeviceDetached -> getString(R.string.app_is_minimised) + getString(R.string.usb_device_detached)
    else -> getString(R.string.app_is_minimised)
}
