package com.ryanamaral.arykey.module.apps.repo

import com.ryanamaral.arykey.module.apps.model.AppItem


interface AppsRepository {

    fun getAppItem(): AppItem?

    fun getInstalledAppList(): List<AppItem>
}
