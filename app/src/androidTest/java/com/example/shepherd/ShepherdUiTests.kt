package com.example.shepherd

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShepherdUiTests {

    // 1. MEMBER DASHBOARD
    @Test
    fun testMemberDashboardLoadsAndNavigatesToAnnouncements() {
        ActivityScenario.launch(MemberHomeActivity::class.java)

        onView(withId(R.id.btnPrayerRequests)).check(matches(isDisplayed()))
        onView(withId(R.id.btnEvents)).check(matches(isDisplayed()))
        onView(withId(R.id.btnBible)).check(matches(isDisplayed()))
        onView(withId(R.id.btnAnnouncements)).check(matches(isDisplayed()))
        onView(withId(R.id.btnNotifications)).check(matches(isDisplayed()))
        onView(withId(R.id.btnProfile)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSettings)).check(matches(isDisplayed()))

        onView(withId(R.id.btnAnnouncements)).perform(click())
        onView(withText("Announcements")).check(matches(isDisplayed()))
    }

    // 2. ANNOUNCEMENTS SCREEN
    @Test
    fun testAnnouncementsScreenLaunchesAndDisplaysContent() {
        ActivityScenario.launch(AnnouncementsActivity::class.java)

        onView(withText("Announcements")).check(matches(isDisplayed()))
        onView(withId(R.id.rvAnnouncements)).check(matches(isDisplayed()))
    }

    // 3. BIBLE SCREEN
    @Test
    fun testBibleScreenLaunchesAndDisplaysControls() {
        ActivityScenario.launch(BibleActivity::class.java)

        onView(withId(R.id.spinnerTestament)).check(matches(isDisplayed()))
        onView(withId(R.id.spinnerBook)).check(matches(isDisplayed()))
        onView(withId(R.id.spinnerChapter)).check(matches(isDisplayed()))
        onView(withId(R.id.tvDailyVerse)).check(matches(isDisplayed()))
        onView(withId(R.id.tvDailyReference)).check(matches(isDisplayed()))
    }

    // 4. EVENTS SCREEN
    @Test
    fun testEventsScreenLaunchesAndDisplaysList() {
        ActivityScenario.launch(EventsActivity::class.java)

        onView(withId(R.id.eventsContainer)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()))
    }

    // 5. SETTINGS SCREEN
    @Test
    fun testSettingsScreenLaunchesAndDisplaysOptions() {
        ActivityScenario.launch(SettingsActivity::class.java)

        onView(withId(R.id.btnChangeLanguage)).check(matches(isDisplayed()))
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()))
    }
}
