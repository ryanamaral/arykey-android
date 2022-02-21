package com.ryanamaral.arykey.module.usb

import android.os.Binder
import com.ryanamaral.arykey.common.extension.weakReference


/**
 * This Binder implementation is a standalone public class,
 * not a inner class like Android documentation demonstrates,
 * because we want to avoid memory leaks that were encountered during debugging.
 *
 * Related answer: https://stackoverflow.com/a/5072908/904907
 * Docs: https://developer.android.com/guide/components/bound-services
 */
class UsbBinder(mUsbService: UsbService) : Binder() {

    var usbService: UsbService? by weakReference(mUsbService)

    fun getService() = usbService
}
