package com.ryanamaral.arykey

import FakeAppsRepositoryImpl
import com.google.common.truth.Truth
import com.ryanamaral.arykey.module.apps.viewmodel.AppsViewModel
import com.ryanamaral.arykey.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


/**
 * JUnit, default unit testing framework built-in android studio.
 * It uses our machine's JVM rather than an Android device or emulator to run tests.
 * It means that is faster but limited in the types of tests we can run.
 */
@RunWith(JUnit4::class)
class AppsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule var coroutineRule = MainCoroutineRule()
    lateinit var appsViewModel: AppsViewModel


    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        appsViewModel =
            AppsViewModel(
                repo = FakeAppsRepositoryImpl(),
                dispatcher = coroutineRule.testDispatcher
            )
    }

    @Test
    fun `App name consumed from repository`() {
        val result = appsViewModel.getAppItem()?.name
        Truth.assertThat(result).isEqualTo("FakeApp")
    }

    @Test
    fun `App package name consumed from repository`() {
        val result = appsViewModel.getAppItem()?.packageName
        Truth.assertThat(result).isEqualTo("com.fake.app")
    }
}
