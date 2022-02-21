package com.ryanamaral.arykey.module.account.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanamaral.arykey.di.IoDispatcher
import com.ryanamaral.arykey.module.account.model.AccountItem
import com.ryanamaral.arykey.module.account.repo.AccountRepository
import com.ryanamaral.arykey.common.domain.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repo: AccountRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _status = MutableLiveData<Result<List<AccountItem>>>()
    val status: MutableLiveData<Result<List<AccountItem>>> = _status

    fun getAccountItem(): AccountItem? = repo.getAccountItem()

    fun startAccountListTask() {
        viewModelScope.launch {
            status.postValue(Result.Loading)
            doAccountListTask()
                .flowOn(dispatcher)
                .catch {
                    status.postValue(Result.Error(it))
                }
                .collect {
                    status.postValue(Result.Success(it))
                }
        }
    }

    private fun doAccountListTask(): Flow<List<AccountItem>> = flow {
        emit(repo.getUserAccountList())
    }
}
