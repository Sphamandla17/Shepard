package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailsActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore

    private var eventId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_event_details)

        firestore = FirebaseFirestore.getInstance()

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        val btnRegister =
            findViewById<Button>(R.id.btnRegister)

        eventId =
            intent.getStringExtra("eventId") ?: ""

        // BACK
        btnBack.setOnClickListener {
            finish()
        }

        // CHECK EVENT ID
        if (eventId.isEmpty()) {

            Toast.makeText(
                this,
                "Event could not be found",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // LOAD EVENT
        loadEvent()

        // REGISTER
        btnRegister.setOnClickListener {

            val intent =
                Intent(
                    this,
                    EventRegistrationActivity::class.java
                )

            intent.putExtra(
                "eventId",
                eventId
            )

            startActivity(intent)
        }
    }

    private fun loadEvent() {

        firestore.collection("events")
            .document(eventId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    Toast.makeText(
                        this,
                        "Event could not be found",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return@addOnSuccessListener
                }

                val title =
                    document.getString("title") ?: ""

                val description =
                    document.getString("description") ?: ""

                val date =
                    document.getString("date") ?: ""

                val time =
                    document.getString("time") ?: ""

                val location =
                    document.getString("location") ?: ""

                findViewById<TextView>(
                    R.id.tvEventTitle
                ).text = title

                findViewById<TextView>(
                    R.id.tvEventDate
                ).text = date

                findViewById<TextView>(
                    R.id.tvEventTime
                ).text = time

                findViewById<TextView>(
                    R.id.tvEventLocation
                ).text = location

                findViewById<TextView>(
                    R.id.tvEventDescription
                ).text = description
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to load event",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
