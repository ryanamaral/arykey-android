package com.ryanamaral.arykey.common.flow

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


fun View.onClick(): Flow<Unit> = callbackFlow {
    setOnClickListener {
        trySend(Unit)
    }
    awaitClose { setOnClickListener(null) }
}
