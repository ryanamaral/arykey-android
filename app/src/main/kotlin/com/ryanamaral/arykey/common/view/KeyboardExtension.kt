package com.ryanamaral.arykey.common.view

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


fun View.setKeyboardClose(context: Context) {
    getInputMethodManager(context).hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.setKeyboardCloseAndClearFocus(context: Context) {
    getInputMethodManager(context).hideSoftInputFromWindow(this.windowToken, 0)
    this.clearFocus()
}

fun View.setKeyboardOpen(context: Context) {
    getInputMethodManager(context).showSoftInput(this, 0)
    this.clearFocus()
}

fun View.setKeyboardOpenAndRequestFocus(context: Context) {
    getInputMethodManager(context).showSoftInput(this, 0)
    this.requestFocus()
}

fun View.requestFocus(activity: Activity) {
    if (this.requestFocus()) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}

private fun getInputMethodManager(context: Context): InputMethodManager {
    return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}
