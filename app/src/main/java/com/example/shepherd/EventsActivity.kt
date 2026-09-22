package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore

class EventsActivity : ComponentActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var eventsContainer: LinearLayout
    private lateinit var tvNoEvents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_events)

        firestore = FirebaseFirestore.getInstance()

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        eventsContainer =
            findViewById(R.id.eventsContainer)

        tvNoEvents =
            findViewById(R.id.tvNoEvents)

        // BACK
        btnBack.setOnClickListener {
            finish()
        }

        // LOAD EVENTS
        loadEvents()
    }

    override fun onResume() {
        super.onResume()

        if (::eventsContainer.isInitialized) {
            loadEvents()
        }
    }

    private fun loadEvents() {

        eventsContainer.removeAllViews()
        tvNoEvents.visibility = TextView.GONE

        firestore.collection("events")
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty()) {

                    tvNoEvents.visibility =
                        TextView.VISIBLE

                    return@addOnSuccessListener
                }

                for (document in documents) {

                    val eventId =
                        document.id

                    val title =
                        document.getString("title")
                            ?: getString(R.string.untitled_event)

                    val date =
                        document.getString("date")
                            ?: ""

                    val time =
                        document.getString("time")
                            ?: ""

                    val location =
                        document.getString("location")
                            ?: ""

                    createEventCard(
                        eventId,
                        title,
                        date,
                        time,
                        location
                    )
                }
            }
            .addOnFailureListener {

                tvNoEvents.visibility =
                    TextView.VISIBLE

                tvNoEvents.text =
                    getString(R.string.failed_load_events)

                Toast.makeText(
                    this,
                    getString(R.string.failed_load_events),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun createEventCard(
        eventId: String,
        title: String,
        date: String,
        time: String,
        location: String
    ) {

        val card =
            CardView(this)

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            0,
            0,
            24
        )

        card.layoutParams =
            cardParams

        card.radius = 16f
        card.cardElevation = 0f

        val cardLayout =
            LinearLayout(this)

        cardLayout.orientation =
            LinearLayout.VERTICAL

        cardLayout.setPadding(
            24,
            24,
            24,
            24
        )

        cardLayout.setBackgroundResource(
            R.drawable.bg_card
        )

        val titleView =
            TextView(this)

        titleView.text =
            title

        titleView.setTextColor(
            getColor(R.color.black)
        )

        titleView.textSize = 22f

        titleView.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val dateView =
            TextView(this)

        dateView.text =
            date

        dateView.setTextColor(
            getColor(R.color.grey_dark)
        )

        dateView.textSize = 16f

        val dateParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        dateParams.topMargin = 12

        dateView.layoutParams =
            dateParams

        val timeView =
            TextView(this)

        timeView.text =
            time

        timeView.setTextColor(
            getColor(R.color.grey_dark)
        )

        timeView.textSize = 14f

        val timeParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        timeParams.topMargin = 4

        timeView.layoutParams =
            timeParams

        val locationView =
            TextView(this)

        locationView.text =
            location

        locationView.setTextColor(
            getColor(R.color.grey_dark)
        )

        locationView.textSize = 14f

        val locationParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        locationParams.topMargin = 8

        locationView.layoutParams =
            locationParams

        val viewButton =
            Button(this)

        viewButton.text =
            getString(R.string.view_event)

        viewButton.setTextColor(
            getColor(R.color.white)
        )

        viewButton.isAllCaps = false

        viewButton.setBackgroundResource(
            R.drawable.bg_button_primary
        )

        val buttonParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                56
            )

        buttonParams.topMargin = 16

        viewButton.layoutParams =
            buttonParams

        viewButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    EventDetailsActivity::class.java
                )

            intent.putExtra(
                "eventId",
                eventId
            )

            startActivity(intent)
        }

        cardLayout.addView(titleView)
        cardLayout.addView(dateView)
        cardLayout.addView(timeView)
        cardLayout.addView(locationView)
        cardLayout.addView(viewButton)

        card.addView(cardLayout)

        eventsContainer.addView(card)
    }


}
