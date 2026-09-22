package com.example.shepherd

import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlin.concurrent.thread

class BibleActivity : ComponentActivity() {

    private lateinit var versesContainer: LinearLayout
    private lateinit var tvBibleTitle: TextView
    private lateinit var tvChapterHeading: TextView
    private lateinit var tvDailyVerse: TextView
    private lateinit var tvDailyReference: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_bible)

        tvBibleTitle =
            findViewById(R.id.tvBibleTitle)

        tvChapterHeading =
            findViewById(R.id.tvChapterHeading)

        tvDailyVerse =
            findViewById(R.id.tvDailyVerse)

        tvDailyReference =
            findViewById(R.id.tvDailyReference)

        versesContainer =
            findViewById(R.id.llVerses)

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        loadBibleChapter()
    }

    private fun loadBibleChapter() {

        val apiUrl =
            "https://bible.helloao.org/api/BSB/JHN/3.simple.json"

        thread {

            try {

                val url =
                    URL(apiUrl)

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode =
                    connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) {

                    throw Exception(
                        "API returned $responseCode"
                    )
                }

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                connection.disconnect()

                runOnUiThread {

                    displayBibleChapter(
                        response
                    )
                }

            } catch (e: Exception) {

                runOnUiThread {

                    Toast.makeText(
                        this,
                        "Failed to load Bible chapter",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun displayBibleChapter(
        jsonResponse: String
    ) {

        try {

            val json =
                JSONObject(jsonResponse)

            val chapter =
                json.getJSONObject("chapter")

            val chapterNumber =
                chapter.getInt("number")

            val content =
                chapter.getJSONArray("content")

            tvBibleTitle.text =
                "John $chapterNumber"

            tvChapterHeading.text =
                "John $chapterNumber"

            versesContainer.removeAllViews()

            var firstVerseText = ""
            var firstVerseNumber = 0

            for (i in 0 until content.length()) {

                val item =
                    content.getJSONObject(i)

                val type =
                    item.getString("type")

                when (type) {

                    "heading" -> {

                        val heading =
                            item.getString("text")

                        addHeading(
                            heading
                        )
                    }

                    "verse" -> {

                        val verseNumber =
                            item.getInt("number")

                        val verseText =
                            item.getString("text")

                        if (firstVerseText.isEmpty()) {

                            firstVerseText =
                                verseText

                            firstVerseNumber =
                                verseNumber
                        }

                        addVerse(
                            verseNumber,
                            verseText
                        )
                    }

                    "line_break" -> {

                        addSpacing()
                    }
                }
            }

            if (firstVerseText.isNotEmpty()) {

                tvDailyVerse.text =
                    firstVerseText

                tvDailyReference.text =
                    "John 3:$firstVerseNumber"
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to read Bible data",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun addHeading(
        heading: String
    ) {

        val textView =
            TextView(this)

        textView.text =
            heading

        textView.setTextColor(
            resources.getColor(
                R.color.black,
                theme
            )
        )

        textView.textSize =
            20f

        textView.setTypeface(
            null,
            Typeface.BOLD
        )

        textView.setPadding(
            0,
            24,
            0,
            12
        )

        versesContainer.addView(
            textView
        )
    }

    private fun addVerse(
        number: Int,
        text: String
    ) {

        val verseLayout =
            LinearLayout(this)

        verseLayout.orientation =
            LinearLayout.HORIZONTAL

        verseLayout.gravity =
            Gravity.TOP

        verseLayout.setPadding(
            0,
            8,
            0,
            8
        )

        val numberText =
            TextView(this)

        numberText.text =
            number.toString()

        numberText.setTextColor(
            resources.getColor(
                R.color.grey_dark,
                theme
            )
        )

        numberText.textSize =
            14f

        numberText.setTypeface(
            null,
            Typeface.BOLD
        )

        val numberParams =
            LinearLayout.LayoutParams(
                40,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        verseLayout.addView(
            numberText,
            numberParams
        )

        val verseText =
            TextView(this)

        verseText.text =
            text

        verseText.setTextColor(
            resources.getColor(
                R.color.black,
                theme
            )
        )

        verseText.textSize =
            17f

        verseText.setLineSpacing(
            4f,
            1f
        )

        val textParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        verseLayout.addView(
            verseText,
            textParams
        )

        versesContainer.addView(
            verseLayout
        )
    }

    private fun addSpacing() {

        val spacing =
            TextView(this)

        spacing.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8
            )

        versesContainer.addView(
            spacing
        )
    }
}