package com.ryanamaral.arykey.di

import android.content.Context
import com.ryanamaral.arykey.module.packagename.data.PackageRepository
import com.ryanamaral.arykey.module.packagename.data.SharedPackageName
import com.ryanamaral.arykey.module.usb.SharedUsbManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PackageModule {

    @Provides
    @Singleton
    fun provideSharedUsbManager(
        @ApplicationContext context: Context,
        @ApplicationScope externalScope: CoroutineScope
    ) = SharedUsbManager(context, externalScope)

    @Provides
    @Singleton
    fun provideSharedPackageName(
        @ApplicationContext context: Context,
        @ApplicationScope externalScope: CoroutineScope
    ) = SharedPackageName(context, externalScope)

    @Provides
    @Singleton
    fun providePackageRepository(sharedPackageName: SharedPackageName) =
        PackageRepository(sharedPackageName)
}
