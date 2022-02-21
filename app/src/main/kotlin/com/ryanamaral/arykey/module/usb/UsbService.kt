package com.ryanamaral.arykey.module.usb

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.felhr.usbserial.CDCSerialDevice
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.utils.ProtocolBuffer
import com.ryanamaral.arykey.BuildConfig
import com.ryanamaral.arykey.module.auth.repo.DeviceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.charset.Charset
import javax.inject.Inject


@AndroidEntryPoint
class UsbService : LifecycleService() {

    @Inject lateinit var usbRepository: UsbRepository
    @Inject lateinit var deviceRepository: DeviceRepository
    private val binder = UsbBinder(this)
    private lateinit var usbManager: UsbManager
    private var device: UsbDevice? = null
    private var connection: UsbDeviceConnection? = null
    private var serialPort: UsbSerialDevice? = null
    private var serialPortConnected = false
    private var connectionJob: Job? = null
    private val protocolBuffer = ProtocolBuffer(ProtocolBuffer.TEXT)

    // get a reference to the Job from the Flow so we can stop it from UI events
    private var usbActionFlow: Job? = null
    private lateinit var notificationManager: NotificationManager

    /*
     * Checks whether the bound activity has really gone away
     * (foreground service with notification created) or simply orientation change (no-op)
     */
    private var configurationChange = false
    private var serviceRunningInForeground = false

    // we save a local reference to last UsbAction to create a Notification if the user navigates away from the app
    private var currentUsbAction: UsbAction? = null


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        // MainActivity (client) comes into foreground and binds to service, so the service can
        // become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false

        return binder
    }

    override fun onRebind(intent: Intent) {
        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        // MainActivity (client) leaves foreground, so service needs to become a foreground service
        // If this method is called due to a configuration change in MainActivity, we do nothing
        if (!configurationChange) {
            val notification = buildUsbNotification(notificationManager, currentUsbAction)
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }
        // Ensures onRebind() is called if MainActivity (client) rebinds
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(CANCEL_USB_STATE_NOTIFICATION, false) == true) {
            unsubscribeToUsbUpdates()
            stopSelf()
        }
        // tells the system not to recreate the service after it's been killed
        return super.onStartCommand(intent, flags, START_NOT_STICKY)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        usbManager = getSystemService(USB_SERVICE) as UsbManager
        findSerialPortDevice()

        usbRepository.isConnect.observeForever { isConnect ->
            if (isConnect) {
                startConnection()
            } else {
                stopConnection()
            }
        }
        usbRepository.settingsEvent.observeForever {
            if (serialPortConnected) {
                serialPort?.close()
                startConnection()
            }
        }
        usbRepository.sendData.observe(this, { data ->
            if (data.isNotEmpty()) write(data)
        })
        subscribeToUsbUpdates()
    }

    override fun onDestroy() {
        serialPort?.close()
        super.onDestroy()
    }

    @ExperimentalCoroutinesApi
    private fun subscribeToUsbUpdates() {
        startService(Intent(applicationContext, UsbService::class.java))
        // binding to this service doesn't trigger onStartCommand()

        usbActionFlow = usbRepository.getUsbAction()
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .buffer(Channel.CONFLATED) // handle back pressure by dropping oldest
            .filterNotNull()
            .distinctUntilChanged() // filter out subsequent repetition of values
            .onEach { usbAction ->
                Timber.d("UsbAction: $usbAction")
                currentUsbAction = usbAction

                when (usbAction) {
                    UsbAction.PermissionGranted -> {
                        usbRepository.isUSBReady = true
                        usbRepository.changePermission(UsbPermission.Granted)
                    }
                    UsbAction.PermissionNotGranted -> {
                        usbRepository.changePermission(UsbPermission.NotGranted)
                    }
                    UsbAction.DeviceAttached -> {
                        if (!serialPortConnected) {
                            findSerialPortDevice()
                        }
                    }
                    UsbAction.DeviceDetached -> {
                        usbRepository.isUSBReady = false
                        usbRepository.changeState(UsbState.Disconnected)
                        if (serialPortConnected) {
                            serialPort?.close()
                            serialPortConnected = false
                        }
                    }
                }
                // updates notification content if this service is running as a foreground service
                if (serviceRunningInForeground) {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        buildUsbNotification(notificationManager, usbAction)
                    )
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun unsubscribeToUsbUpdates() {
        usbActionFlow?.cancel()
    }

    private fun write(data: String) {
        serialPort?.write(data.toByteArray(Charset.defaultCharset()))
    }

    private fun findSerialPortDevice() {
        val usbDevices = usbManager.deviceList
        if (usbDevices.isEmpty()) {
            usbRepository.changeState(UsbState.NoUsb)
            return
        }
        for ((_, value) in usbDevices) {
            device = value
            if (device != null && UsbSerialDevice.isSupported(device)) {
                requestUserPermission()
                break
            } else {
                connection = null
                device = null
            }
        }
        if (device == null) {
            usbRepository.changeState(UsbState.NoUsb)
        }
    }

    private fun requestUserPermission() {
        PendingIntent.getBroadcast(
            this,
            0,
            Intent(ACTION_USB_PERMISSION),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        ).let { intent ->
            usbManager.requestPermission(device, intent)
        }
    }

    private fun startConnection() {
        connectionJob = lifecycleScope.launch {
            connection = usbManager.openDevice(device)
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection)
            serialPort?.let { usbSerialDevice ->
                if (usbSerialDevice.open()) {
                    serialPortConnected = true
                    protocolBuffer.setDelimiter(
                        deviceRepository.getLineFeedCode(
                            SerialPortConfiguration.lineFeedCodeSend
                        )
                    )
                    usbSerialDevice.apply {
                        setBaudRate(SerialPortConfiguration.baudRate)
                        setDataBits(SerialPortConfiguration.dataBits)
                        setStopBits(SerialPortConfiguration.stopBits)
                        setParity(SerialPortConfiguration.parity)
                        setFlowControl(SerialPortConfiguration.flowControl)
                        read { bytes ->
                            protocolBuffer.appendData(bytes)
                            while (protocolBuffer.hasMoreCommands()) {
                                usbRepository.updateReceivedData(protocolBuffer.nextTextCommand())
                            }
                        }
                    }
                    usbRepository.changeState(UsbState.Ready)

                } else {
                    if (serialPort is CDCSerialDevice) {
                        usbRepository.changeState(UsbState.CdcDriverNotWorking)
                    } else {
                        usbRepository.changeState(UsbState.UsbDeviceNotWorking)
                    }
                }
            } ?: run {
                usbRepository.changeState(UsbState.NotSupported)
            }
        }
    }

    private fun stopConnection() {
        connectionJob?.cancel()
        connection?.let {
            it.close()
            connection = null
        }
        if (serialPortConnected) {
            serialPort?.close()
            serialPort = null
            serialPortConnected = false
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 0x003
        const val ACTION_USB_PERMISSION =
            "${BuildConfig.APPLICATION_ID}.USB_PERMISSION"
        const val NOTIFICATION_CHANNEL_ID =
            "usb_state_channel"
        const val CANCEL_USB_STATE_NOTIFICATION =
            "${BuildConfig.APPLICATION_ID}.CANCEL_USB_STATE_NOTIFICATION"
    }
}
