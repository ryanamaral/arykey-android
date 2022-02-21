package com.ryanamaral.arykey.module.usb


/**
 * Constants matching the device's firmware configuration
 */
object SerialPortConfiguration {
    const val baudRate: Int = 115200
    const val dataBits: Int = 8
    const val stopBits: Int = 1
    const val parity: Int = 0
    const val flowControl: Int = 0
    const val state: Boolean = false
    const val lineFeedCodeSend: String = "cr_lf"
}
