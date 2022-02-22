package com.ryanamaral.arykey.common.view

import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


fun Snackbar.removeBottomPadding() {
    view.elevation = 0F // remove elevation as with it comes a bottom padding
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        // setting the bottom padding to the same as the top does the trick
        v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, v.paddingTop)
        insets
    }
    // remove padding at the bottom when using android 10 gesture navigation
    isGestureInsetBottomIgnored = true
}

fun Snackbar.addDismissListener(
    onDismissed: (event: Int) -> Unit = {}
) {
    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onShown(transientBottomBar: Snackbar?) {
            super.onShown(transientBottomBar)
        }

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            onDismissed(event)
        }
    })
}
