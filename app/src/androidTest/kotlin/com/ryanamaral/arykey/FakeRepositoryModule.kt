package com.ryanamaral.arykey


import FakeAccountRepositoryImpl
import FakeAppsRepositoryImpl
import com.ryanamaral.arykey.di.RepositoryModule
import com.ryanamaral.arykey.module.account.repo.AccountRepository
import com.ryanamaral.arykey.module.apps.repo.AppsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.testing.TestInstallIn


@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [RepositoryModule::class]
)
@Module
class FakeRepositoryModule {

    @Provides
    fun provideAppsRepository(): AppsRepository = FakeAppsRepositoryImpl()

    @Provides
    fun provideAccountRepository(): AccountRepository = FakeAccountRepositoryImpl()
}
