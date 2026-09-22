package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemberHomeActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var currentLanguage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLanguage =
            LanguageHelper.getSavedLanguage(this)

        setContentView(R.layout.activity_member_home)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val tvWelcomeMessage =
            findViewById<TextView>(R.id.tvWelcomeMessage)

        val btnPrayerRequests =
            findViewById<Button>(R.id.btnPrayerRequests)

        val btnEvents =
            findViewById<Button>(R.id.btnEvents)

        val btnNotifications =
            findViewById<Button>(R.id.btnNotifications)

        val btnQrCheckIn =
            findViewById<Button>(R.id.btnQrCheckIn)

        val btnProfile =
            findViewById<Button>(R.id.btnProfile)

        val btnSettings =
            findViewById<Button>(R.id.btnSettings)

        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val firstName =
                            document.getString("firstName")
                                ?: "Member"

                        tvWelcomeMessage.text =
                            if (
                                LanguageHelper.getSavedLanguage(this)
                                == "zu"
                            ) {
                                "Wamukelekile, $firstName"
                            } else {
                                "Welcome, $firstName"
                            }
                    }
                }
        }

        btnPrayerRequests.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PrayerRequestsActivity::class.java
                )
            )
        }

        btnEvents.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    EventsActivity::class.java
                )
            )
        }

        btnNotifications.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    NotificationsActivity::class.java
                )
            )
        }

        btnQrCheckIn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    QrCheckinActivity::class.java
                )
            )
        }

        btnProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        btnSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        val savedLanguage =
            LanguageHelper.getSavedLanguage(this)

        if (
            currentLanguage.isNotEmpty() &&
            savedLanguage.isNotEmpty() &&
            currentLanguage != savedLanguage
        ) {
            recreate()
        }
    }
}