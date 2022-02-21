package com.ryanamaral.arykey.di

import com.ryanamaral.arykey.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient().newBuilder().apply {
            // timeout in making the initial connection; i.e. completing the TCP connection handshake
            connectTimeout(BuildConfig.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            // timeout on waiting to read data
            readTimeout(BuildConfig.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            retryOnConnectionFailure(true)
            if (BuildConfig.BUILD_TYPE == "debug") {
                addInterceptor(HttpLoggingInterceptor { message -> Timber.i(message) }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
    }
}
