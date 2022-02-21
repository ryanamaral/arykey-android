package com.ryanamaral.arykey.module.auth.repo

import android.content.Context
import com.ryanamaral.arykey.R
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DeviceRepository @Inject constructor(
    private val context: Context
) {
    fun getLineFeedCode(lineFeedCode: String) = when (lineFeedCode) {
        context.getString(R.string.line_feed_code_cr_value) -> CR
        context.getString(R.string.line_feed_code_lf_value) -> LF
        else -> CR_LF
    }

    companion object {
        const val CR_LF = "\r\n"
        const val LF = "\n"
        const val CR = "\r"
    }
}
