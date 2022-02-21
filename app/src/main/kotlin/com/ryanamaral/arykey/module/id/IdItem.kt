package com.ryanamaral.arykey.module.id

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class IdItem(
    var appPackageName: String,
    var accountUserId: String
) : Parcelable
