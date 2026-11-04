package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query

class PrayerRequestsActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: MemberPrayerRequestAdapter

    private val prayerRequests =
        mutableListOf<PrayerRequest>()

    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_prayer_requests)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        val btnSubmitPrayer =
            findViewById<Button>(R.id.btnSubmitPrayer)

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.rvPrayerRequests
            )

        btnBack.setOnClickListener {
            finish()
        }

        btnSubmitPrayer.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PrayerRequestFormActivity::class.java
                )
            )
        }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        adapter =
            MemberPrayerRequestAdapter(
                prayerRequests
            )

        recyclerView.adapter = adapter

        loadPrayerRequests()
    }

    private fun loadPrayerRequests() {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please log in again",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        listenerRegistration?.remove()

        listenerRegistration = firestore.collection("prayerRequests")
            .whereEqualTo(
                "userId",
                currentUser.uid
            )
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, error ->

                if (error != null) {
                    if (NetworkUtils.isInternetAvailable(this)) {
                        Toast.makeText(
                            this,
                            "Failed to load prayer requests: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@addSnapshotListener
                }

                if (snapshots != null) {

                    prayerRequests.clear()

                    for (document in snapshots.documents) {

                        val hasPending =
                            document.metadata.hasPendingWrites()

                        val rawStatus =
                            document.getString("status")
                                ?: "Received"

                        val status =
                            if (hasPending) {
                                "Pending Sync"
                            } else {
                                rawStatus
                            }

                        val request =
                            PrayerRequest(
                                id = document.id,

                                userId =
                                    document.getString(
                                        "userId"
                                    ) ?: "",

                                category =
                                    document.getString(
                                        "category"
                                    ) ?: "",

                                requestText =
                                    document.getString(
                                        "requestText"
                                    ) ?: "",

                                status = status,

                                pastorFeedback =
                                    document.getString(
                                        "pastorFeedback"
                                    ) ?: "",

                                locationShared =
                                    document.getBoolean(
                                        "locationShared"
                                    ) ?: false
                            )

                        prayerRequests.add(request)
                    }

                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            loadPrayerRequests()
        }
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
