package com.ryanamaral.arykey.module.apps.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanamaral.arykey.di.IoDispatcher
import com.ryanamaral.arykey.common.domain.Result
import com.ryanamaral.arykey.module.apps.repo.AppsRepository
import com.ryanamaral.arykey.module.apps.model.AppItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppsViewModel @Inject constructor(
    private val repo: AppsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _status = MutableLiveData<Result<List<AppItem>>>()
    val status : MutableLiveData<Result<List<AppItem>>> = _status

    fun getAppItem(): AppItem? = repo.getAppItem()

    fun startAppListTask() {
        viewModelScope.launch {
            _status.postValue(Result.Loading)
            doAppListTask()
                .flowOn(dispatcher)
                .catch {
                    _status.postValue(Result.Error(it))
                }
                .collect {
                    _status.postValue(Result.Success(it))
                }
        }
    }

    private fun doAppListTask(): Flow<List<AppItem>> = flow {
        emit(repo.getInstalledAppList())
    }
}
