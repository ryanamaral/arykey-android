package com.ryanamaral.arykey.common.flow

import android.view.View
import androidx.annotation.CheckResult
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive


/**
 * Create a flow which emits the state change events from [View] on [BottomSheetBehavior].
 *
 * *Note:* A value will be emitted immediately.
 *
 * Examples:
 *
 * ```
 * // handle initial value
 * bottomSheetBehavior.stateChanges()
 *      .onEach { /* handle state change */ }
 *      .flowWithLifecycle(lifecycle)
 *      .launchIn(lifecycleScope) // lifecycle-runtime-ktx
 *
 * // drop initial value
 * bottomSheetBehavior.stateChanges()
 *      .dropInitialValue()
 *      .onEach { /* handle state change */ }
 *      .flowWithLifecycle(lifecycle)
 *      .launchIn(lifecycleScope) // lifecycle-runtime-ktx
 * ```
 *
 * src: https://github.com/LDRAlighieri/Corbind/blob/437226e19ef69a934ced7415077a058d0cae753f/corbind-material/src/main/kotlin/ru/ldralighieri/corbind/material/BottomSheetBehaviorStateChanges.kt
 */
fun View.stateChanges(): InitialValueFlow<Int> = channelFlow {
    val behavior = getBehavior(this@stateChanges)
    val callback = callback(this, ::trySend)
    behavior.addBottomSheetCallback(callback)
    awaitClose { behavior.removeBottomSheetCallback(callback) }
}.asInitialValueFlow(getBehavior(this@stateChanges).state)


@CheckResult
private fun getBehavior(view: View): BottomSheetBehavior<*> {
    val params = view.layoutParams as? CoordinatorLayout.LayoutParams
        ?: throw IllegalArgumentException("The view is not in a Coordinator Layout.")
    return params.behavior as BottomSheetBehavior<*>?
        ?: throw IllegalStateException("There's no behavior set on this view.")
}


@CheckResult
private fun callback(
    scope: CoroutineScope,
    emitter: (Int) -> Unit
) = object : BottomSheetBehavior.BottomSheetCallback() {

    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (scope.isActive) emitter(newState)
    }
}

class InitialValueFlow<T>(private val flow: Flow<T>) : Flow<T> by flow {
    fun dropInitialValue(): Flow<T> = drop(1)
    suspend fun asStateFlow(scope: CoroutineScope): StateFlow<T> = stateIn(scope)
}

fun <T> Flow<T>.asInitialValueFlow(value: T): InitialValueFlow<T> = InitialValueFlow(
    onStart { emit(value) }
)
