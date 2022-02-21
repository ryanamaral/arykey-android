package com.ryanamaral.arykey.module.apps.repo

import android.content.Context
import android.content.pm.ApplicationInfo
import com.ryanamaral.arykey.module.apps.model.AppItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AppsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppsRepository {

    override fun getAppItem(): AppItem? = null

    override fun getInstalledAppList(): List<AppItem> {
        val appList: MutableList<AppItem> = mutableListOf()
        context.packageManager?.getInstalledApplications(0)?.forEach { element ->
            if (element.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                appList.add(
                    AppItem(
                        name = context.packageManager.getApplicationLabel(element).toString(),
                        packageName = element.packageName
                    )
                )
            }
        }
        return appList
    }
}
