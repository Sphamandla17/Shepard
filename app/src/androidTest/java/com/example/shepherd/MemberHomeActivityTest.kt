package com.example.shepherd

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemberHomeActivityTest {

    @Test
    fun memberHomeScreenLoadsCorrectly() {

        ActivityScenario.launch(
            MemberHomeActivity::class.java
        )

        onView(
            withId(R.id.btnPrayerRequests)
        ).check(
            matches(isDisplayed())
        )

        onView(
            withId(R.id.btnEvents)
        ).check(
            matches(isDisplayed())
        )

        onView(
            withId(R.id.btnBible)
        ).check(
            matches(isDisplayed())
        )

        onView(
            withId(R.id.btnAnnouncements)
        ).check(
            matches(isDisplayed())
        )

        onView(
            withId(R.id.btnNotifications)
        ).check(
            matches(isDisplayed())
        )

        onView(
            withId(R.id.btnProfile)
        ).check(
            matches(isDisplayed())
        )

        onView(
            withId(R.id.btnSettings)
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun announcementsButtonOpensAnnouncementsScreen() {

        ActivityScenario.launch(
            MemberHomeActivity::class.java
        )

        onView(
            withId(R.id.btnAnnouncements)
        ).perform(
            click()
        )

        onView(
            withText("Announcements")
        ).check(
            matches(isDisplayed())
        )
    }
}