package com.example.shepherd

import android.content.ContentValues
import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class BibleRepository(context: Context) {

    private val dbHelper = BibleDatabaseHelper(context)
    private val translationId = "BSB"

    fun getBooks(): List<BibleBook> {
        val books = mutableListOf<BibleBook>()
        try {
            val url = URL("https://bible.helloao.org/api/BSB/books.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val booksArray = json.getJSONArray("books")

                for (i in 0 until booksArray.length()) {
                    val obj = booksArray.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val testament = obj.optString("testament", "Old Testament")
                    val chaptersCount = obj.getJSONArray("chapters").length()

                    books.add(BibleBook(id, name, testament, chaptersCount))
                }
            }
            connection.disconnect()
        } catch (_: Exception) {
            // Fallback or empty if offline
        }

        if (books.isEmpty()) {
            books.addAll(getFallbackBooks())
        }
        return books
    }

    private fun getFallbackBooks(): List<BibleBook> {
        return listOf(
            BibleBook("GEN", "Genesis", "Old Testament", 50),
            BibleBook("EXO", "Exodus", "Old Testament", 40),
            BibleBook("PSA", "Psalms", "Old Testament", 150),
            BibleBook("MAT", "Matthew", "New Testament", 28),
            BibleBook("JHN", "John", "New Testament", 21),
            BibleBook("ROM", "Romans", "New Testament", 16)
        )
    }

    fun getChapter(bookId: String, bookName: String, testament: String, chapterNumber: Int): List<BibleVerseItem> {
        val verses = mutableListOf<BibleVerseItem>()

        val db = dbHelper.readableDatabase
        val cursor = db.query(
            BibleDatabaseHelper.TABLE_VERSES,
            arrayOf(BibleDatabaseHelper.COLUMN_VERSE, BibleDatabaseHelper.COLUMN_TEXT, BibleDatabaseHelper.COLUMN_HEADING),
            "${BibleDatabaseHelper.COLUMN_TRANSLATION} = ? AND ${BibleDatabaseHelper.COLUMN_BOOK_ID} = ? AND ${BibleDatabaseHelper.COLUMN_CHAPTER} = ?",
            arrayOf(translationId, bookId, chapterNumber.toString()),
            null, null, "${BibleDatabaseHelper.COLUMN_VERSE} ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val verseNum = cursor.getInt(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_VERSE))
                val verseText = cursor.getString(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_TEXT))
                val heading = cursor.getString(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_HEADING))

                if (!heading.isNullOrEmpty()) {
                    verses.add(BibleVerseItem(type = "heading", heading = heading))
                }
                if (verseNum > 0) {
                    verses.add(BibleVerseItem(type = "verse", number = verseNum, text = verseText))
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        if (verses.isNotEmpty()) {
            return verses
        }

        try {
            val url = URL("https://bible.helloao.org/api/BSB/$bookId/$chapterNumber.simple.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val chapterObj = json.getJSONObject("chapter")
                val contentArray = chapterObj.getJSONArray("content")

                val writeDb = dbHelper.writableDatabase
                writeDb.beginTransaction()
                try {
                    var currentHeading: String? = null
                    for (i in 0 until contentArray.length()) {
                        val item = contentArray.getJSONObject(i)
                        val type = item.getString("type")

                        when (type) {
                            "heading" -> {
                                currentHeading = item.getString("text")
                                verses.add(BibleVerseItem(type = "heading", heading = currentHeading))
                            }
                            "verse" -> {
                                val vNum = item.getInt("number")
                                val vText = item.getString("text")
                                verses.add(BibleVerseItem(type = "verse", number = vNum, text = vText))

                                val values = ContentValues().apply {
                                    put(BibleDatabaseHelper.COLUMN_TRANSLATION, translationId)
                                    put(BibleDatabaseHelper.COLUMN_BOOK_ID, bookId)
                                    put(BibleDatabaseHelper.COLUMN_BOOK_NAME, bookName)
                                    put(BibleDatabaseHelper.COLUMN_TESTAMENT, testament)
                                    put(BibleDatabaseHelper.COLUMN_CHAPTER, chapterNumber)
                                    put(BibleDatabaseHelper.COLUMN_VERSE, vNum)
                                    put(BibleDatabaseHelper.COLUMN_TEXT, vText)
                                    put(BibleDatabaseHelper.COLUMN_HEADING, currentHeading)
                                }
                                writeDb.insert(BibleDatabaseHelper.TABLE_VERSES, null, values)
                                currentHeading = null
                            }
                            "line_break" -> {
                                verses.add(BibleVerseItem(type = "line_break"))
                            }
                        }
                    }
                    writeDb.setTransactionSuccessful()
                } finally {
                    writeDb.endTransaction()
                }
            }
            connection.disconnect()
        } catch (_: Exception) {
            // Offline or network error
        }

        return verses
    }

    fun searchVerses(query: String): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val db = dbHelper.readableDatabase
        val searchQuery = "%$query%"

        val cursor = db.query(
            BibleDatabaseHelper.TABLE_VERSES,
            arrayOf(BibleDatabaseHelper.COLUMN_BOOK_NAME, BibleDatabaseHelper.COLUMN_CHAPTER, BibleDatabaseHelper.COLUMN_VERSE, BibleDatabaseHelper.COLUMN_TEXT),
            "${BibleDatabaseHelper.COLUMN_TEXT} LIKE ? AND ${BibleDatabaseHelper.COLUMN_VERSE} > 0",
            arrayOf(searchQuery),
            null, null, null, "50"
        )

        if (cursor.moveToFirst()) {
            do {
                val bookName = cursor.getString(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_BOOK_NAME))
                val chapter = cursor.getInt(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_CHAPTER))
                val verse = cursor.getInt(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_VERSE))
                val text = cursor.getString(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_TEXT))

                results.add(SearchResult(bookName, chapter, verse, text))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return results
    }

    fun getVerseByReference(bookNameQuery: String, chapter: Int, verse: Int): SearchResult? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            BibleDatabaseHelper.TABLE_VERSES,
            arrayOf(BibleDatabaseHelper.COLUMN_BOOK_NAME, BibleDatabaseHelper.COLUMN_CHAPTER, BibleDatabaseHelper.COLUMN_VERSE, BibleDatabaseHelper.COLUMN_TEXT),
            "${BibleDatabaseHelper.COLUMN_BOOK_NAME} LIKE ? AND ${BibleDatabaseHelper.COLUMN_CHAPTER} = ? AND ${BibleDatabaseHelper.COLUMN_VERSE} = ?",
            arrayOf("%$bookNameQuery%", chapter.toString(), verse.toString()),
            null, null, null, "1"
        )

        var result: SearchResult? = null
        if (cursor.moveToFirst()) {
            val bookName = cursor.getString(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_BOOK_NAME))
            val ch = cursor.getInt(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_CHAPTER))
            val v = cursor.getInt(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_VERSE))
            val text = cursor.getString(cursor.getColumnIndexOrThrow(BibleDatabaseHelper.COLUMN_TEXT))
            result = SearchResult(bookName, ch, v, text)
        }
        cursor.close()
        return result
    }

    fun isBibleDownloaded(): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${BibleDatabaseHelper.TABLE_VERSES}", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count > 31000
    }
}
