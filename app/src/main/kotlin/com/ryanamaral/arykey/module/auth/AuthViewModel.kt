package com.ryanamaral.arykey.module.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanamaral.arykey.di.IoDispatcher
import com.ryanamaral.arykey.common.domain.Result
import com.ryanamaral.arykey.module.id.IdItem
import com.ryanamaral.arykey.module.usb.UsbRepository
import com.ryanamaral.arykey.module.usb.UsbWriteResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val usbRepository: UsbRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _status = MutableLiveData<Result<UsbWriteResult>>()
    val status: MutableLiveData<Result<UsbWriteResult>> = _status


    fun startSendMessageTask(idItem: IdItem, pin: String) {
        viewModelScope.launch {
            status.postValue(Result.Loading)
            doSendMessageTask(idItem, pin)
                .flowOn(dispatcher)
                .catch {
                    status.postValue(Result.Error(it))
                }
                .map {
                    if (it?.isNotEmpty() == true) {
                        UsbWriteResult.Success
                    } else {
                        UsbWriteResult.Error
                    }
                }
                .collect {
                    status.postValue(Result.Success(it))
                }
        }
    }

    private fun doSendMessageTask(idItem: IdItem, pin: String): Flow<String?> = flow {
        emit(sendMessageToConnectedDevice(idItem, pin))
    }

    private fun sendMessageToConnectedDevice(idItem: IdItem, pin: String): String? {
        if (pin.isNotEmpty()) {
            try {
                //todo: hash message
                Timber.d("sha512: ${sha512(pin)}")

                usbRepository.sendData(pin)
                Timber.d("SendMessage: $pin")
                return pin

            } catch (e: UnsupportedEncodingException) {
                Timber.e(e.toString())
            }
        }
        return null
    }
}

fun sha512(input: String): String {
    return MessageDigest.getInstance("SHA-512")
        .digest(input.toByteArray())
        .joinToString(separator = "") {
            ((it.toInt() and 0xff) + 0x100)
                .toString(16)
                .substring(1)
        }
}
