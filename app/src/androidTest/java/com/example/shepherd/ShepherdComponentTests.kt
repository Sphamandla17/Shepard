package com.example.shepherd

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShepherdComponentTests {

    private lateinit var context: Context
    private lateinit var dbHelper: BibleDatabaseHelper

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dbHelper = BibleDatabaseHelper(context)
    }

    @After
    fun tearDown() {
        try {
            dbHelper.close()
        } catch (_: Exception) {}
    }

    // 1. LanguageHelper Test
    @Test
    fun testLanguageHelperSaveAndRetrieve() {
        val originalLang = LanguageHelper.getSavedLanguage(context)

        try {
            LanguageHelper.saveLanguage(context, "en")
            assertEquals("en", LanguageHelper.getSavedLanguage(context))

            LanguageHelper.saveLanguage(context, "zu")
            assertEquals("zu", LanguageHelper.getSavedLanguage(context))
        } finally {
            LanguageHelper.saveLanguage(context, originalLang)
        }
    }

    // 2. NetworkUtils Test
    @Test
    fun testNetworkUtilsConnectivityCheckAndCallback() {
        val isAvailable = NetworkUtils.isInternetAvailable(context)
        // Verify the method executes successfully and yields a boolean result
        val result: Boolean = isAvailable
        assertNotNull(result)

        val callback = NetworkUtils.registerNetworkCallback(
            context,
            onAvailable = {},
            onValidated = {},
            onInternetLost = {},
            onLost = {}
        )
        NetworkUtils.unregisterNetworkCallback(context, callback)
    }

    // 3. BibleRepository Fallback Books Test
    @Test
    fun testBibleRepositoryFallbackBooks() {
        val repository = BibleRepository(context)
        val books = repository.getBooks()
        assertNotNull(books)
        assertTrue(books.isNotEmpty())

        val genesis = books.find { it.id == "GEN" }
        assertNotNull(genesis)
        assertEquals("Genesis", genesis?.name)
        assertEquals("Old Testament", genesis?.testament)
        assertEquals(50, genesis?.chaptersCount)

        val john = books.find { it.id == "JHN" }
        assertNotNull(john)
        assertEquals("John", john?.name)
        assertEquals("New Testament", john?.testament)
        assertEquals(21, john?.chaptersCount)
    }

    // 4. BibleDatabaseHelper Schema Test
    @Test
    fun testBibleDatabaseSchemaCreation() {
        val db = dbHelper.writableDatabase
        assertNotNull(db)
        assertTrue(db.isOpen)

        val cursorVerses = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(BibleDatabaseHelper.TABLE_VERSES)
        )
        assertTrue(cursorVerses.moveToFirst())
        assertEquals(BibleDatabaseHelper.TABLE_VERSES, cursorVerses.getString(0))
        cursorVerses.close()

        val cursorDownloads = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(BibleDatabaseHelper.TABLE_DOWNLOADS)
        )
        assertTrue(cursorDownloads.moveToFirst())
        assertEquals(BibleDatabaseHelper.TABLE_DOWNLOADS, cursorDownloads.getString(0))
        cursorDownloads.close()

        val cursorColumns = db.rawQuery("PRAGMA table_info(${BibleDatabaseHelper.TABLE_VERSES})", null)
        val columnNames = mutableSetOf<String>()
        if (cursorColumns.moveToFirst()) {
            do {
                val colName = cursorColumns.getString(cursorColumns.getColumnIndexOrThrow("name"))
                columnNames.add(colName)
            } while (cursorColumns.moveToNext())
        }
        cursorColumns.close()

        assertTrue(columnNames.contains(BibleDatabaseHelper.COLUMN_TRANSLATION))
        assertTrue(columnNames.contains(BibleDatabaseHelper.COLUMN_BOOK_ID))
        assertTrue(columnNames.contains(BibleDatabaseHelper.COLUMN_BOOK_NAME))
        assertTrue(columnNames.contains(BibleDatabaseHelper.COLUMN_CHAPTER))
        assertTrue(columnNames.contains(BibleDatabaseHelper.COLUMN_VERSE))
        assertTrue(columnNames.contains(BibleDatabaseHelper.COLUMN_TEXT))
    }
}
