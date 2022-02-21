package com.ryanamaral.arykey.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


/**
 * https://medium.com/androiddevelopers/create-an-application-coroutinescope-using-hilt-dd444e721528
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope


@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {

    @Singleton
    @ApplicationScope
    @Provides
    fun provideCoroutineScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope {
        // run this code when providing an instance of CoroutineScope
        return CoroutineScope(SupervisorJob() + defaultDispatcher)
    }
}
