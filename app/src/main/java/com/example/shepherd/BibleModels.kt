package com.example.shepherd

data class BibleBook(
    val id: String,
    val name: String,
    val testament: String,
    val chaptersCount: Int
)

data class BibleVerseItem(
    val type: String,
    val number: Int = 0,
    val text: String = "",
    val heading: String? = null
)

data class SearchResult(
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val text: String
)
