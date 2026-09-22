package com.example.shepherd

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var rvNotifications: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                loadNotificationDocuments(language)
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Could not load your language: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun loadNotificationDocuments(
        language: String
    ) {

        firestore
            .collection("notifications")
            .orderBy(
                "createdAt",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { documents ->

                val notifications =
                    mutableListOf<NotificationItem>()

                for (document in documents) {

                    val title =
                        if (language == "zu") {
                            document.getString("zuluTitle")
                                ?: ""
                        } else {
                            document.getString("englishTitle")
                                ?: ""
                        }

                    val message =
                        if (language == "zu") {
                            document.getString("zuluMessage")
                                ?: ""
                        } else {
                            document.getString("englishMessage")
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
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Could not load notifications: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}