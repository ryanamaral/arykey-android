package com.ryanamaral.arykey.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ryanamaral.arykey.data.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * EncryptedSharedPreferences - Wraps the SharedPreferences class and
     * automatically encrypts keys and values using a two-scheme method:
     * - Keys are encrypted using a deterministic encryption algorithm such that the key can be encrypted and properly looked up.
     * - Values are encrypted using AES-256 GCM and are non-deterministic.
     */
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): AppPreferences =
        AppPreferences(
            EncryptedSharedPreferences.create(
                context,
                "preferences.enc",
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        )
}
