package com.ryanamaral.arykey.common.extension

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


fun Fragment.buildSnackbar(
    view: CoordinatorLayout,
    messageStringResId: Int,
    actionStringResId: Int,
    onDismissed: (event: Int) -> Unit = {}
): Snackbar {
    return Snackbar.make(view, getString(messageStringResId), Snackbar.LENGTH_INDEFINITE).apply {
        view.elevation = 0F //FIXME: elevation is not being applied
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            //FIXME: paddingBottom is not correct
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, v.paddingBottom)
            insets
        }
        isGestureInsetBottomIgnored = true
        setAction(actionStringResId) { dismiss() }
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
}
