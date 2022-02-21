package com.ryanamaral.arykey.di

import com.ryanamaral.arykey.module.account.repo.AccountRepository
import com.ryanamaral.arykey.module.account.repo.AccountRepositoryImpl
import com.ryanamaral.arykey.module.apps.repo.AppsRepository
import com.ryanamaral.arykey.module.apps.repo.AppsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindAppsRepository(repo: AppsRepositoryImpl): AppsRepository

    @Binds
    abstract fun bindAccountRepository(repo: AccountRepositoryImpl): AccountRepository
}
