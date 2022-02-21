package com.ryanamaral.arykey.data

import android.content.SharedPreferences


class AppPreferences(private val sharedPreferences: SharedPreferences) {

    /**
     * Persist last selected account of a given packageName
     */
    fun setLastSelectedAccount(packageName: String, account: String) {
        sharedPreferences.edit().putString(packageName, account).apply()
    }

    /**
     * Returns last selected account of a given packageName
     */
    fun getLastSelectedAccount(packageName: String): String? {
        return sharedPreferences.getString(packageName, null)
    }
}
