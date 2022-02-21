package com.ryanamaral.arykey.module.packagename.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow


class PackageRepository constructor(
    private val sharedPackageName: SharedPackageName
) {
    /**
     * Status of whether the app is actively subscribed to package changes
     */
    val receivingPackageUpdates: StateFlow<Boolean> = sharedPackageName.receivingPackageUpdates

    /**
     * Observable flow for package updates
     */
    @ExperimentalCoroutinesApi
    fun getPackages() = sharedPackageName.packageFlow()
}
