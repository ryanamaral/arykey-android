package com.ryanamaral.arykey.common.flow

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch


/**
 * src: https://github.com/Kotlin/kotlinx.coroutines/issues/1446#issuecomment-625244176
 */
fun <T> Flow<T>.throttleFirst(windowDuration: Long = 1000L): Flow<T> {
    var job: Job = Job().apply {
        complete()
    }
    return onCompletion { job.cancel() }.run {
        flow {
            coroutineScope {
                collect { value ->
                    if (!job.isActive) {
                        emit(value)
                        job = launch { delay(windowDuration) }
                    }
                }
            }
        }
    }
}
