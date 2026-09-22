package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PastorHomeActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var currentLanguage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLanguage =
            LanguageHelper.getSavedLanguage(this)

        loadPastorHome()
    }

    private fun loadPastorHome() {

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_pastor_home)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val tvWelcome =
            findViewById<TextView>(R.id.tvWelcome)

        val btnPastorMembers =
            findViewById<Button>(R.id.btnPastorMembers)

        val btnNotifications =
            findViewById<Button>(R.id.btnNotifications)

        val btnProfile =
            findViewById<Button>(R.id.btnProfile)

        val btnSettings =
            findViewById<Button>(R.id.btnSettings)

        loadPastorName(tvWelcome)

        // ==========================================
        // OPEN CHURCH MEMBERS
        // ==========================================

        btnPastorMembers.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AdminMembersActivity::class.java
                )
            )
        }

        // ==========================================
        // OPEN NOTIFICATIONS
        // ==========================================

        btnNotifications.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    NotificationsActivity::class.java
                )
            )
        }

        // ==========================================
        // OPEN PROFILE
        // ==========================================

        btnProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // ==========================================
        // OPEN SETTINGS
        // ==========================================

        btnSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }

    private fun loadPastorName(
        tvWelcome: TextView
    ) {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {
            return
        }

        firestore
            .collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val firstName =
                        document.getString("firstName")
                            ?: "Pastor"

                    val language =
                        LanguageHelper.getSavedLanguage(this)

                    tvWelcome.text =
                        if (language == "zu") {
                            "Wamukelekile, Mfundisi $firstName"
                        } else {
                            "Welcome, Pastor $firstName"
                        }
                }
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

            currentLanguage = savedLanguage

            loadPastorHome()
        }
    }


}
