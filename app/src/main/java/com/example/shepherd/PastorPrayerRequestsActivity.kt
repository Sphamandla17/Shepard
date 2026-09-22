package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PastorPrayerRequestsActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: PastorPrayerRequestAdapter

    private val prayerRequests = mutableListOf<PrayerRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_pastor_prayer_requests)

        firestore = FirebaseFirestore.getInstance()

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.rvPastorPrayerRequests
            )

        btnBack.setOnClickListener {
            finish()
        }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        adapter = PastorPrayerRequestAdapter(
            prayerRequests
        ) { prayerRequest ->

            val intent = Intent(
                this,
                PastorPrayerDetailsActivity::class.java
            )

            intent.putExtra(
                "prayerRequestId",
                prayerRequest.id
            )

            startActivity(intent)
        }

        recyclerView.adapter = adapter

        loadPrayerRequests()
    }

    private fun loadPrayerRequests() {

        firestore
            .collection("prayerRequests")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { documents ->

                prayerRequests.clear()

                for (document in documents) {

                    val request = PrayerRequest(
                        id = document.id,

                        userId =
                            document.getString("userId")
                                ?: "",

                        category =
                            document.getString("category")
                                ?: "",

                        requestText =
                            document.getString("requestText")
                                ?: "",

                        status =
                            document.getString("status")
                                ?: "Received",

                        pastorFeedback =
                            document.getString("pastorFeedback")
                                ?: "",

                        locationShared =
                            document.getBoolean(
                                "locationShared"
                            ) ?: false
                    )

                    prayerRequests.add(request)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to load prayer requests",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            loadPrayerRequests()
        }
    }
}