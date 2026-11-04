package com.example.shepherd

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import kotlin.concurrent.thread

class BibleActivity : ComponentActivity() {

    private lateinit var repository: BibleRepository
    private lateinit var versesContainer: LinearLayout
    private lateinit var tvBibleTitle: TextView
    private lateinit var tvChapterHeading: TextView
    private lateinit var tvDailyVerse: TextView
    private lateinit var tvDailyReference: TextView
    private lateinit var tvDownloadStatus: TextView
    private lateinit var spinnerTestament: Spinner
    private lateinit var spinnerBook: Spinner
    private lateinit var spinnerChapter: Spinner
    private lateinit var etSearchBible: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnDownloadBible: Button
    private lateinit var btnPrevChapter: Button
    private lateinit var btnNextChapter: Button

    private var allBooks = listOf<BibleBook>()
    private var filteredBooks = listOf<BibleBook>()
    private var selectedBook: BibleBook? = null
    private var currentChapter = 1

    private var isInitializing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_bible)

        repository = BibleRepository(this)

        tvBibleTitle = findViewById(R.id.tvBibleTitle)
        tvChapterHeading = findViewById(R.id.tvChapterHeading)
        tvDailyVerse = findViewById(R.id.tvDailyVerse)
        tvDailyReference = findViewById(R.id.tvDailyReference)
        tvDownloadStatus = findViewById(R.id.tvDownloadStatus)
        versesContainer = findViewById(R.id.llVerses)

        spinnerTestament = findViewById(R.id.spinnerTestament)
        spinnerBook = findViewById(R.id.spinnerBook)
        spinnerChapter = findViewById(R.id.spinnerChapter)
        etSearchBible = findViewById(R.id.etSearchBible)
        btnSearch = findViewById(R.id.btnSearch)
        btnDownloadBible = findViewById(R.id.btnDownloadBible)
        btnPrevChapter = findViewById(R.id.btnPrevChapter)
        btnNextChapter = findViewById(R.id.btnNextChapter)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        setupTestamentSpinner()

        btnSearch.setOnClickListener {
            performSearch()
        }

        btnDownloadBible.setOnClickListener {
            downloadCompleteBible()
        }

        btnPrevChapter.setOnClickListener {
            navigateChapter(-1)
        }

        btnNextChapter.setOnClickListener {
            navigateChapter(1)
        }

        loadBooksAndInitialChapter()
    }

    private fun setupTestamentSpinner() {
        val testaments = arrayOf("Old Testament", "New Testament")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, testaments)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTestament.adapter = adapter

        spinnerTestament.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTestament = if (position == 0) "Old Testament" else "New Testament"
                updateBooksForTestament(selectedTestament)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadBooksAndInitialChapter() {
        thread {
            allBooks = repository.getBooks()
            runOnUiThread {
                isInitializing = true
                updateBooksForTestament("Old Testament")
                isInitializing = false

                loadDefaultChapter()
                checkDownloadStatus()
            }
        }
    }

    private fun updateBooksForTestament(testament: String) {
        filteredBooks = allBooks.filter { it.testament.equals(testament, ignoreCase = true) }
        val bookNames = filteredBooks.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBook.adapter = adapter

        spinnerBook.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in filteredBooks.indices) {
                    selectedBook = filteredBooks[position]
                    updateChaptersForBook(selectedBook!!, 1)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateChaptersForBook(book: BibleBook, targetChapter: Int) {
        val chapters = (1..book.chaptersCount).map { "Chapter $it" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, chapters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerChapter.adapter = adapter

        spinnerChapter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isInitializing) {
                    currentChapter = position + 1
                    loadChapter(book, currentChapter)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (targetChapter in 1..book.chaptersCount) {
            currentChapter = targetChapter
            spinnerChapter.setSelection(targetChapter - 1)
            loadChapter(book, currentChapter)
        }
    }

    private fun loadDefaultChapter() {
        val jhn = allBooks.find { it.id == "JHN" } ?: allBooks.firstOrNull()
        if (jhn != null) {
            val testamentIndex = if (jhn.testament.equals("New Testament", true)) 1 else 0
            spinnerTestament.setSelection(testamentIndex)
            updateBooksForTestament(jhn.testament)
            val bookIndex = filteredBooks.indexOfFirst { it.id == jhn.id }
            if (bookIndex >= 0) {
                spinnerBook.setSelection(bookIndex)
                selectedBook = jhn
                updateChaptersForBook(jhn, 3)
            }
        }
    }

    private fun loadChapter(book: BibleBook, chapter: Int) {
        tvChapterHeading.text = "${book.name} $chapter"
        tvBibleTitle.text = "${book.name} $chapter"
        versesContainer.removeAllViews()

        thread {
            val verses = repository.getChapter(book.id, book.name, book.testament, chapter)
            runOnUiThread {
                displayVerses(verses, book.name, chapter)
            }
        }
    }

    private fun displayVerses(verses: List<BibleVerseItem>, bookName: String, chapter: Int) {
        versesContainer.removeAllViews()
        var dailySet = false

        for (item in verses) {
            when (item.type) {
                "heading" -> {
                    item.heading?.let { addHeading(it) }
                }
                "verse" -> {
                    addVerse(item.number, item.text)
                    if (!dailySet) {
                        tvDailyVerse.text = item.text
                        tvDailyReference.text = "$bookName $chapter:${item.number}"
                        dailySet = true
                    }
                }
                "line_break" -> {
                    addSpacing()
                }
            }
        }

        if (verses.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No verses available. Please connect to the internet or download the Bible."
            tv.setPadding(0, 16, 0, 16)
            versesContainer.addView(tv)
        }
    }

    private fun navigateChapter(direction: Int) {
        val currentBook = selectedBook ?: return
        val newChapter = currentChapter + direction

        if (newChapter in 1..currentBook.chaptersCount) {
            currentChapter = newChapter
            spinnerChapter.setSelection(newChapter - 1)
            loadChapter(currentBook, currentChapter)
        } else if (newChapter > currentBook.chaptersCount) {
            val currentBookIndex = filteredBooks.indexOf(currentBook)
            if (currentBookIndex + 1 < filteredBooks.size) {
                val nextBook = filteredBooks[currentBookIndex + 1]
                spinnerBook.setSelection(currentBookIndex + 1)
                selectedBook = nextBook
                updateChaptersForBook(nextBook, 1)
            } else {
                val testIndex = spinnerTestament.selectedItemPosition
                if (testIndex == 0) {
                    spinnerTestament.setSelection(1)
                    updateBooksForTestament("New Testament")
                    if (filteredBooks.isNotEmpty()) {
                        val nextBook = filteredBooks[0]
                        spinnerBook.setSelection(0)
                        selectedBook = nextBook
                        updateChaptersForBook(nextBook, 1)
                    }
                } else {
                    Toast.makeText(this, "End of Bible", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (newChapter < 1) {
            val currentBookIndex = filteredBooks.indexOf(currentBook)
            if (currentBookIndex - 1 >= 0) {
                val prevBook = filteredBooks[currentBookIndex - 1]
                spinnerBook.setSelection(currentBookIndex - 1)
                selectedBook = prevBook
                updateChaptersForBook(prevBook, prevBook.chaptersCount)
            }
        }
    }

    private fun performSearch() {
        val query = etSearchBible.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search query or reference", Toast.LENGTH_SHORT).show()
            return
        }

        val parsedRef = BibleReferenceParser.parse(query)

        if (parsedRef != null) {
            val bookQuery = parsedRef.bookName
            val chapterNum = parsedRef.chapter
            val verseNum = parsedRef.verse

            thread {
                val res = repository.getVerseByReference(bookQuery, chapterNum, verseNum)
                runOnUiThread {
                    if (res != null) {
                        Toast.makeText(this, "Found: ${res.bookName} ${res.chapter}:${res.verse}", Toast.LENGTH_SHORT).show()
                        val book = allBooks.find { it.name.contains(res.bookName, true) }
                        if (book != null) {
                            val testIndex = if (book.testament.equals("New Testament", true)) 1 else 0
                            spinnerTestament.setSelection(testIndex)
                            updateBooksForTestament(book.testament)
                            val bIndex = filteredBooks.indexOfFirst { it.id == book.id }
                            if (bIndex >= 0) {
                                spinnerBook.setSelection(bIndex)
                                selectedBook = book
                                updateChaptersForBook(book, res.chapter)
                            }
                        }
                    } else {
                        Toast.makeText(this, "Reference not found locally. Try downloading the Bible first.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            thread {
                val results = repository.searchVerses(query)
                runOnUiThread {
                    displaySearchResults(results, query)
                }
            }
        }
    }

    private fun displaySearchResults(results: List<SearchResult>, query: String) {
        versesContainer.removeAllViews()
        tvChapterHeading.text = "Search Results for \"$query\" (${results.size})"

        if (results.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No matching verses found. Try downloading the Bible for offline search."
            tv.setPadding(0, 16, 0, 16)
            versesContainer.addView(tv)
            return
        }

        for (item in results) {
            val resLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundResource(R.drawable.bg_card)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16) }
            }

            val refTv = TextView(this).apply {
                text = "${item.bookName} ${item.chapter}:${item.verse}"
                setTypeface(null, Typeface.BOLD)
                textSize = 14f
                setTextColor(resources.getColor(R.color.black, theme))
            }

            val textTv = TextView(this).apply {
                text = item.text
                textSize = 15f
                setTextColor(resources.getColor(R.color.grey_dark, theme))
                setPadding(0, 4, 0, 0)
            }

            resLayout.addView(refTv)
            resLayout.addView(textTv)

            resLayout.setOnClickListener {
                val book = allBooks.find { it.name.contains(item.bookName, true) }
                if (book != null) {
                    val testIndex = if (book.testament.equals("New Testament", true)) 1 else 0
                    spinnerTestament.setSelection(testIndex)
                    updateBooksForTestament(book.testament)
                    val bIndex = filteredBooks.indexOfFirst { it.id == book.id }
                    if (bIndex >= 0) {
                        spinnerBook.setSelection(bIndex)
                        selectedBook = book
                        updateChaptersForBook(book, item.chapter)
                    }
                }
            }

            versesContainer.addView(resLayout)
        }
    }

    private fun downloadCompleteBible() {
        btnDownloadBible.isEnabled = false
        tvDownloadStatus.visibility = View.VISIBLE
        tvDownloadStatus.text = "Preparing Bible download..."

        thread {
            try {
                var totalChapters = 0
                for (b in allBooks) {
                    totalChapters += b.chaptersCount
                }

                var downloaded = 0
                for (b in allBooks) {
                    for (c in 1..b.chaptersCount) {
                        runOnUiThread {
                            tvDownloadStatus.text = "Downloading ${b.name} Chapter $c ($downloaded/$totalChapters)..."
                        }
                        repository.getChapter(b.id, b.name, b.testament, c)
                        downloaded++
                    }
                }

                runOnUiThread {
                    btnDownloadBible.isEnabled = true
                    tvDownloadStatus.text = "Complete BSB Bible downloaded successfully!"
                    Toast.makeText(this, "Complete BSB Bible downloaded!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    btnDownloadBible.isEnabled = true
                    tvDownloadStatus.text = "Download failed: ${e.message}"
                    Toast.makeText(this, "Download failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkDownloadStatus() {
        thread {
            val downloaded = repository.isBibleDownloaded()
            runOnUiThread {
                if (downloaded) {
                    tvDownloadStatus.visibility = View.VISIBLE
                    tvDownloadStatus.text = "Complete BSB Bible available offline."
                    btnDownloadBible.text = "Bible Downloaded"
                    btnDownloadBible.isEnabled = false
                }
            }
        }
    }

    private fun addHeading(heading: String) {
        val textView = TextView(this)
        textView.text = heading
        textView.setTextColor(resources.getColor(R.color.black, theme))
        textView.textSize = 20f
        textView.setTypeface(null, Typeface.BOLD)
        textView.setPadding(0, 24, 0, 12)
        versesContainer.addView(textView)
    }

    private fun addVerse(number: Int, text: String) {
        val verseLayout = LinearLayout(this)
        verseLayout.orientation = LinearLayout.HORIZONTAL
        verseLayout.gravity = Gravity.TOP
        verseLayout.setPadding(0, 8, 0, 8)

        val numberText = TextView(this)
        numberText.text = number.toString()
        numberText.setTextColor(resources.getColor(R.color.grey_dark, theme))
        numberText.textSize = 14f
        numberText.setTypeface(null, Typeface.BOLD)

        val numberParams = LinearLayout.LayoutParams(40, LinearLayout.LayoutParams.WRAP_CONTENT)
        verseLayout.addView(numberText, numberParams)

        val verseText = TextView(this)
        verseText.text = text
        verseText.setTextColor(resources.getColor(R.color.black, theme))
        verseText.textSize = 17f
        verseText.setLineSpacing(4f, 1f)

        val textParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        verseLayout.addView(verseText, textParams)

        versesContainer.addView(verseLayout)
    }

    private fun addSpacing() {
        val spacing = TextView(this)
        spacing.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 8)
        versesContainer.addView(spacing)
    }
}
