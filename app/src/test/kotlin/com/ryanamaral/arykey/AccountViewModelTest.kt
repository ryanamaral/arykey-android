package com.ryanamaral.arykey

import FakeAccountRepositoryImpl
import com.google.common.truth.Truth
import com.ryanamaral.arykey.module.account.viewmodel.AccountViewModel
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
class AccountViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule var coroutineRule = MainCoroutineRule()
    lateinit var accountViewModel: AccountViewModel


    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        accountViewModel =
            AccountViewModel(
                repo = FakeAccountRepositoryImpl(),
                dispatcher = coroutineRule.testDispatcher
            )
    }

    @Test
    fun `Account email consumed from repository`() {
        val result = accountViewModel.getAccountItem()?.email
        Truth.assertThat(result).isEqualTo("fake@email.com")
    }
}
