package com.ryanamaral.arykey

import android.content.Context
import com.google.common.truth.Truth
import com.ryanamaral.arykey.module.auth.repo.DeviceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test


/**
 * [MockK | mocking library for Kotlin](https://mockk.io/)
 * To be able to mock final classes (they are all final in kotlin)
 * without changing them, like adding the "open" keyword.
 * It also enable us to create stubs by using the `mockk` and `every` function.
 * Stubbing allows us to setup predefined answers when functions are called.
 * +more:
 * https://www.baeldung.com/kotlin/mockk
 * https://notwoods.github.io/mockk-guidebook/docs/mocking/stubbing/
 *
 * [Truth - Fluent assertions for Java and Android](https://truth.dev/)
 * To make our test assertions and failure messages more readable.
 * Seemingly 'cleaner' than JUnit, AssertJ and Hamcrest.
 * It is used in the majority of the tests in Google’s own codebase.
 */
class DeviceRepositoryTest {

    /**
     * Relaxed so we don't need to provide mock implementations for methods we don't use
     */
    @MockK(relaxed = true)
    val context = mockk<Context>()

    private val repository = DeviceRepository(context)


    @Before
    fun init() {
        MockKAnnotations.init(this)
        every { context.getString(R.string.line_feed_code_cr_value) } returns "cr"
        every { context.getString(R.string.line_feed_code_lf_value) } returns "lf"
    }

    @Test
    fun `With line feed code cr return cr`() {
        val result = repository.getLineFeedCode("cr")
        Truth.assertThat(result).isEqualTo("\r")
    }

    @Test
    fun `With line feed code lf return lf`() {
        val result = repository.getLineFeedCode("lf")
        Truth.assertThat(result).isEqualTo("\n")
    }

    @Test
    fun `With any line feed code return crlf`() {
        val result = repository.getLineFeedCode("any_chars")
        Truth.assertThat(result).isEqualTo("\r\n")
    }
}
