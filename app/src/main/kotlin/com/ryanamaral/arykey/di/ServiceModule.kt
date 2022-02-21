package com.ryanamaral.arykey.di

import com.ryanamaral.arykey.module.usb.UsbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {

    @Provides
    @Singleton
    fun provideUsbService() = UsbService()
}
