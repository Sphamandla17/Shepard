package com.example.shepherd

import com.example.shepherd.api.Announcement
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ShepherdUnitTests {

    /*
     * NOTE ON ANDROID CONTEXT & SQLITE DEPENDENCIES (Items 1, 2, 3, 4):
     * - LanguageHelper, NetworkUtils, BibleRepository, and BibleDatabaseHelper depend on Android Context,
     *   SharedPreferences, ConnectivityManager, and SQLiteOpenHelper.
     * - As pure local JVM unit tests (JUnit without Robolectric or mocked Context), these components
     *   cannot run against the Android framework and correctly belong in instrumented tests (androidTest).
     */

    // 5. Announcement model: verify Gson JSON parsing, including nullable imageUrl
    @Test
    fun testAnnouncementJsonParsingWithNonNullImageUrl() {
        val json = """
            {
                "id": 1,
                "title": "Sunday Service",
                "message": "Join us this Sunday",
                "createdBy": "Pastor John",
                "createdAt": "2026-06-04",
                "imageUrl": "http://172.1.16.216:5211/images/test.jpg"
            }
        """.trimIndent()

        val announcement = Gson().fromJson(json, Announcement::class.java)

        assertNotNull(announcement)
        assertEquals(1, announcement.id)
        assertEquals("Sunday Service", announcement.title)
        assertEquals("Join us this Sunday", announcement.message)
        assertEquals("Pastor John", announcement.createdBy)
        assertEquals("2026-06-04", announcement.createdAt)
        assertEquals("http://172.1.16.216:5211/images/test.jpg", announcement.imageUrl)
    }

    @Test
    fun testAnnouncementJsonParsingWithNullImageUrl() {
        val json = """
            {
                "id": 2,
                "title": "Prayer Meeting",
                "message": "Midweek prayer",
                "createdBy": "Pastor Mary",
                "createdAt": "2026-06-05",
                "imageUrl": null
            }
        """.trimIndent()

        val announcement = Gson().fromJson(json, Announcement::class.java)

        assertNotNull(announcement)
        assertEquals(2, announcement.id)
        assertEquals("Prayer Meeting", announcement.title)
        assertNull(announcement.imageUrl)
    }

    // 6. Notification model: verify NotificationItem properties
    @Test
    fun testNotificationItemProperties() {
        val notification = NotificationItem(
            title = "New Announcement",
            message = "Church meeting tomorrow",
            createdAt = null
        )

        assertEquals("New Announcement", notification.title)
        assertEquals("Church meeting tomorrow", notification.message)
        assertNull(notification.createdAt)
    }

    // 7. Bible reference parser: verify parsing using BibleReferenceParser
    @Test
    fun testBibleReferenceParserValidReference() {
        val parsed = BibleReferenceParser.parse("John 3:16")
        assertNotNull(parsed)
        assertEquals("John", parsed?.bookName)
        assertEquals(3, parsed?.chapter)
        assertEquals(16, parsed?.verse)
    }

    @Test
    fun testBibleReferenceParserNumberedBookReference() {
        val parsed = BibleReferenceParser.parse("1 John 1:9")
        assertNotNull(parsed)
        assertEquals("1 John", parsed?.bookName)
        assertEquals(1, parsed?.chapter)
        assertEquals(9, parsed?.verse)
    }

    @Test
    fun testBibleReferenceParserInvalidReference() {
        val parsed = BibleReferenceParser.parse("John 3") // Missing verse
        assertNull(parsed)
    }

    // 8. SearchResult model: verify actual model properties and behavior
    @Test
    fun testSearchResultModelProperties() {
        val searchResult = SearchResult(
            bookName = "John",
            chapter = 3,
            verse = 16,
            text = "For God so loved the world..."
        )

        assertEquals("John", searchResult.bookName)
        assertEquals(3, searchResult.chapter)
        assertEquals(16, searchResult.verse)
        assertEquals("For God so loved the world...", searchResult.text)
    }
}
