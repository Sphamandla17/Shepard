package com.example.shepherd

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminEventsActivity : AppCompatActivity() {

    private lateinit var rvAdminEvents: RecyclerView
    private lateinit var btnCreateEvent: Button

    private val db = FirebaseFirestore.getInstance()

    private val eventList = mutableListOf<EventItem>()

    private lateinit var adapter: AdminEventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved language
        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_admin_events)

        rvAdminEvents = findViewById(R.id.rvAdminEvents)
        btnCreateEvent = findViewById(R.id.btnCreateEvent)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // RecyclerView
        adapter = AdminEventsAdapter(
            eventList
        ) { event ->

            val intent = android.content.Intent(
                this,
                AdminEventRegistrationsActivity::class.java
            )

            intent.putExtra(
                "eventId",
                event.eventId
            )

            startActivity(intent)
        }

        rvAdminEvents.layoutManager =
            LinearLayoutManager(this)

        rvAdminEvents.adapter = adapter

        // Create Event
        btnCreateEvent.setOnClickListener {

            val intent = android.content.Intent(
                this,
                CreateEventActivity::class.java
            )

            startActivity(intent)
        }

        loadEvents()
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            loadEvents()
        }
    }

    private fun loadEvents() {

        db.collection("events")
            .get()
            .addOnSuccessListener { documents ->

                eventList.clear()

                for (document in documents) {

                    val eventId =
                        document.id

                    val title =
                        document.getString("title")
                            ?: getString(R.string.event_title)

                    val date =
                        document.getString("date")
                            ?: ""

                    val time =
                        document.getString("time")
                            ?: ""

                    val location =
                        document.getString("location")
                            ?: ""

                    eventList.add(
                        EventItem(
                            eventId = eventId,
                            title = title,
                            date = date,
                            time = time,
                            location = location
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    getString(R.string.failed_load_events),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}