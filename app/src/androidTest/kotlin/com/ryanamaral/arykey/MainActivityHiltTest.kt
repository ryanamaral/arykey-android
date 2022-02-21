package com.ryanamaral.arykey

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityHiltTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testNameMatchesDataSource() {
        // Inject the dependencies to the test (if there is any @Inject field in the test)
        hiltRule.inject()
        Espresso.onView(ViewMatchers.withId(R.id.textViewTitle))
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText("FakeApp")
                    )
                )
            )
    }
}
