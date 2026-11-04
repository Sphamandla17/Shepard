package com.example.shepherd

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query

class NotificationsActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var rvNotifications: RecyclerView
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_notifications)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        rvNotifications =
            findViewById(R.id.rvNotifications)

        rvNotifications.layoutManager =
            LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        loadNotifications()
    }

    private fun loadNotifications() {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in again.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        val userId =
            currentUser.uid

        // First get the user's saved language
        firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocument ->

                val language =
                    userDocument.getString("language")
                        ?: "en"

                listenToNotifications(language)
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Could not load your language: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                listenToNotifications("en")
            }
    }

    private fun listenToNotifications(
        language: String
    ) {

        listenerRegistration?.remove()

        listenerRegistration = firestore
            .collection("notifications")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, exception ->

                if (exception != null) {
                    if (NetworkUtils.isInternetAvailable(this)) {
                        Toast.makeText(
                            this,
                            "Could not load notifications: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@addSnapshotListener
                }

                if (snapshots != null) {

                    val notifications =
                        mutableListOf<NotificationItem>()

                    for (document in snapshots.documents) {

                        val title =
                            if (language == "zu") {
                                document.getString("zuluTitle")
                                    ?: document.getString("englishTitle")
                                    ?: ""
                            } else {
                                document.getString("englishTitle")
                                    ?: document.getString("zuluTitle")
                                    ?: ""
                            }

                        val message =
                            if (language == "zu") {
                                document.getString("zuluMessage")
                                    ?: document.getString("englishMessage")
                                    ?: ""
                            } else {
                                document.getString("englishMessage")
                                    ?: document.getString("zuluMessage")
                                    ?: ""
                            }

                        val createdAt =
                            document.getTimestamp("createdAt")

                        notifications.add(
                            NotificationItem(
                                title = title,
                                message = message,
                                createdAt = createdAt
                            )
                        )
                    }

                    rvNotifications.adapter =
                        NotificationAdapter(notifications)
                }
            }
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
