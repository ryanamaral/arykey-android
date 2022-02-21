package com.ryanamaral.arykey.module.usb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UsbRepository @Inject constructor(
    private val sharedUsbManager: SharedUsbManager
) {
    /**
     * Status of whether the app is actively subscribed to package changes
     */
    val receivingUsbUpdates: StateFlow<Boolean> = sharedUsbManager.receivingUsbUpdates

    /**
     * Observable flow for package updates
     */
    @ExperimentalCoroutinesApi
    fun getUsbAction() = sharedUsbManager.usbFlow()


    private val _sendData = MutableLiveData("")
    val sendData: LiveData<String> = _sendData
    fun sendData(data: String) = _sendData.postValue(data)

    var isUSBReady = false

    private val _isConnect = MutableLiveData(false)
    val isConnect: LiveData<Boolean> = _isConnect

    private val _state = MutableLiveData<UsbState>(UsbState.Initialized)
    val state: LiveData<UsbState> = _state

    private val _permission = MutableLiveData<UsbPermission>()
    val permission: LiveData<UsbPermission> = _permission

    private val _settingsEvent = MutableLiveData<Unit>()
    val settingsEvent: LiveData<Unit> = _settingsEvent

    fun changeState(state: UsbState) = _state.postValue(state)
    fun changePermission(permission: UsbPermission) = _permission.postValue(permission)
    fun changeSerialSettings() = _settingsEvent.postValue(Unit)
    fun changeConnection(isConnect: Boolean) = _isConnect.postValue(isConnect)

    fun updateReceivedData(data: String) {
        Timber.e("Received Data: $data")
        //TODO: to be implemented
    }
}
