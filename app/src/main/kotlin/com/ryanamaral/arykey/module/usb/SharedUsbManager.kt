package com.ryanamaral.arykey.module.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber


/**
 * Wraps the Permission & Attached events into callbackFlow
 */
class SharedUsbManager constructor(
    private val context: Context,
    externalScope: CoroutineScope
) {
    val receivingUsbUpdates: StateFlow<Boolean>
        get() = _receivingUsbUpdates

    private val _receivingUsbUpdates: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @ExperimentalCoroutinesApi
    fun usbFlow(): Flow<UsbAction> = _usbUpdates

    @ExperimentalCoroutinesApi
    private val _usbUpdates = callbackFlow<UsbAction> {

        val usbReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    UsbService.ACTION_USB_PERMISSION -> {
                        val extra = intent.extras ?: return
                        val granted = extra.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)
                        if (granted) {
                            trySend(UsbAction.PermissionGranted)
                        } else {
                            trySend(UsbAction.PermissionNotGranted)
                        }
                    }
                    UsbManager.ACTION_USB_DEVICE_ATTACHED -> trySend(UsbAction.DeviceAttached)
                    UsbManager.ACTION_USB_DEVICE_DETACHED -> trySend(UsbAction.DeviceDetached)
                    else -> Timber.e("Unknown action")
                }
            }
        }
        Timber.d("Starting USB updates")
        _receivingUsbUpdates.value = true

        context.registerReceiver(usbReceiver, IntentFilter().apply {
            addAction(UsbService.ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        })

        awaitClose {
            Timber.d("Stopping USB updates")
            _receivingUsbUpdates.value = false
            context.unregisterReceiver(usbReceiver)
        }
    }.shareIn( // make cold flow hot
        externalScope, // make the flow follow the externalScope
        replay = 0,
        started = SharingStarted.WhileSubscribed() // keep the producer active while there are active subscribers
    )
}
