package com.example.shepherd

data class BibleReference(
    val bookName: String,
    val chapter: Int,
    val verse: Int
)

object BibleReferenceParser {
    private val refRegex = Regex("""^([1-3]?\s*[A-Za-z]+)\s+(\d+):(\d+)$""")

    fun parse(query: String): BibleReference? {
        val match = refRegex.find(query.trim()) ?: return null
        val bookName = match.groupValues[1].trim()
        val chapter = match.groupValues[2].toIntOrNull() ?: return null
        val verse = match.groupValues[3].toIntOrNull() ?: return null
        return BibleReference(bookName, chapter, verse)
    }
}
