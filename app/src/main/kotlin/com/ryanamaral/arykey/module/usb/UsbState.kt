package com.ryanamaral.arykey.module.usb


sealed class UsbState {
    object Initialized : UsbState()
    object Ready : UsbState()
    object NotSupported : UsbState()
    object NoUsb : UsbState()
    object Disconnected : UsbState()
    object CdcDriverNotWorking : UsbState()
    object UsbDeviceNotWorking : UsbState()
}

sealed class UsbPermission {
    object Granted : UsbPermission()
    object NotGranted : UsbPermission()
}

sealed class UsbAction {
    object PermissionGranted : UsbAction()
    object PermissionNotGranted : UsbAction()
    object DeviceAttached : UsbAction()
    object DeviceDetached : UsbAction()
}

sealed class UsbWriteResult {
    object Success : UsbWriteResult()
    object Error : UsbWriteResult()
}
