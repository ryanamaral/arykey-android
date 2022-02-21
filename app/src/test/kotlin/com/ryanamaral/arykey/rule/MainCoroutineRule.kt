package com.ryanamaral.arykey.rule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description


/**
 * Source:
 *  - https://github.com/google/iosched/
 *  - https://github.com/android/architecture-components-samples
 */
@ExperimentalCoroutinesApi
class MainCoroutineRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}

fun MainCoroutineRule.runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
    this.testDispatcher.runBlockingTest {
        block()
    }

/**
 * Creates a new [CoroutineScope] with the rule's testDispatcher
 */
fun MainCoroutineRule.CoroutineScope(): CoroutineScope = CoroutineScope(testDispatcher)
