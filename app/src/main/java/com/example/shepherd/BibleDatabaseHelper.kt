package com.example.shepherd

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BibleDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "shepherd_bible.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_VERSES = "verses"
        const val COLUMN_ID = "id"
        const val COLUMN_TRANSLATION = "translation_id"
        const val COLUMN_BOOK_ID = "book_id"
        const val COLUMN_BOOK_NAME = "book_name"
        const val COLUMN_TESTAMENT = "testament"
        const val COLUMN_CHAPTER = "chapter_number"
        const val COLUMN_VERSE = "verse_number"
        const val COLUMN_TEXT = "verse_text"
        const val COLUMN_HEADING = "heading"

        const val TABLE_DOWNLOADS = "downloads"
        const val COLUMN_DL_TRANSLATION = "translation_id"
        const val COLUMN_DL_TOTAL = "total_chapters"
        const val COLUMN_DL_CURRENT = "downloaded_chapters"
        const val COLUMN_DL_COMPLETE = "is_complete"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createVersesTable = """
            CREATE TABLE $TABLE_VERSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TRANSLATION TEXT,
                $COLUMN_BOOK_ID TEXT,
                $COLUMN_BOOK_NAME TEXT,
                $COLUMN_TESTAMENT TEXT,
                $COLUMN_CHAPTER INTEGER,
                $COLUMN_VERSE INTEGER,
                $COLUMN_TEXT TEXT,
                $COLUMN_HEADING TEXT
            )
        """.trimIndent()

        val createDownloadsTable = """
            CREATE TABLE $TABLE_DOWNLOADS (
                $COLUMN_DL_TRANSLATION TEXT PRIMARY KEY,
                $COLUMN_DL_TOTAL INTEGER,
                $COLUMN_DL_CURRENT INTEGER,
                $COLUMN_DL_COMPLETE INTEGER
            )
        """.trimIndent()

        db.execSQL(createVersesTable)
        db.execSQL(createDownloadsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VERSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOWNLOADS")
        onCreate(db)
    }
}
