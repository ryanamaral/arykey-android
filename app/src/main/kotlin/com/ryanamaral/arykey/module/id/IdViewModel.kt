package com.ryanamaral.arykey.module.id

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryanamaral.arykey.module.account.model.AccountItem
import com.ryanamaral.arykey.module.apps.model.AppItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*


/**
 * TODO: Work in progress
 */
class IdViewModel : ViewModel() {

    private val _appName = MutableStateFlow("")
    private val _accountName = MutableStateFlow("")
    private val mutableSelectedApp = MutableLiveData<AppItem>()
    private val mutableSelectedAccount = MutableLiveData<AccountItem>()

    val selectedApp: LiveData<AppItem> get() = mutableSelectedApp
    val selectedAccount: LiveData<AccountItem> get() = mutableSelectedAccount

    fun selectApplicationItem(item: AppItem) {
        mutableSelectedApp.value = item
    }

    fun selectAccount(item: AccountItem) {
        mutableSelectedAccount.value = item
    }

    private val _appItem = MutableSharedFlow<AppItem>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val appItemState = _appItem.distinctUntilChanged() // get StateFlow-like behavior
    val appItem = _appItem.asSharedFlow()


    fun selectAppItem(item: AppItem) {
        _appItem.tryEmit(item)
    }

    fun onAppNameChange(name: String) {
        _appName.value = name
    }

    fun onAccountNameChange(name: String) {
        _accountName.value = name
    }

    val isInputValid: Flow<Boolean> =
        combine(_appName, _accountName) { appName, accountName ->
            return@combine isAppPackageNameValid(appName) and isAccountUserIdValid(accountName)
        }

    fun isAppPackageNameValid(appName: String): Boolean = appName.length > 1

    fun isAccountUserIdValid(accountName: String): Boolean = accountName.length > 1
}
