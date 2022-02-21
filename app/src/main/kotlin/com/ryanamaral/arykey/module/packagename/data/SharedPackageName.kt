package com.ryanamaral.arykey.module.packagename.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ryanamaral.arykey.module.packagename.PackageAccessibilityService.Companion.ACTION_FOREGROUND_APPLICATION_CHANGED
import com.ryanamaral.arykey.module.packagename.PackageAccessibilityService.Companion.EXTRA_CLASS_NAME
import com.ryanamaral.arykey.module.packagename.PackageAccessibilityService.Companion.EXTRA_PACKAGE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber


/**
 * Wraps the PackageBroadcastReceiver in callbackFlow
 */
class SharedPackageName constructor(
    private val context: Context,
    externalScope: CoroutineScope
) {
    /**
     * Status of package updates, i.e., whether the app is actively subscribed to package changes
     */
    val receivingPackageUpdates: StateFlow<Boolean>
        get() = _receivingPackageUpdates

    private val _receivingPackageUpdates: MutableStateFlow<Boolean> = MutableStateFlow(false)


    @ExperimentalCoroutinesApi
    fun packageFlow(): Flow<String> = _packageUpdates

    @ExperimentalCoroutinesApi
    private val _packageUpdates = callbackFlow<String> {

        val packageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == ACTION_FOREGROUND_APPLICATION_CHANGED) {
                    val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME).toString()
                    val className = intent.getStringExtra(EXTRA_CLASS_NAME).toString()
                    Timber.d("New package: $packageName")
                    Timber.d("New className: $className")
                    trySend(packageName)
                }
            }
        }
        Timber.d("Starting package updates")
        _receivingPackageUpdates.value = true

        val intentFilter = IntentFilter(ACTION_FOREGROUND_APPLICATION_CHANGED)
        context.registerReceiver(packageReceiver, intentFilter)

        awaitClose {
            Timber.d("Stopping package updates")
            _receivingPackageUpdates.value = false
            context.unregisterReceiver(packageReceiver)
        }
    }.shareIn( // make cold flow hot
        externalScope, // make the flow follow the externalScope
        replay = 1, // emit the last emitted element to new collectors
        started = SharingStarted.Lazily
    )
}
