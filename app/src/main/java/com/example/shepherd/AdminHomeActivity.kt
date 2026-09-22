package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class AdminHomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadAdminHome()
    }

    private fun loadAdminHome() {

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_admin_home)

        val btnManageMembers =
            findViewById<Button>(R.id.btnManageMembers)

        val btnManageEvents =
            findViewById<Button>(R.id.btnManageEvents)

        val btnCreateEvent =
            findViewById<Button>(R.id.btnCreateEvent)

        val btnNotifications =
            findViewById<Button>(R.id.btnNotifications)

        val btnProfile =
            findViewById<Button>(R.id.btnProfile)

        val btnSettings =
            findViewById<Button>(R.id.btnSettings)


        // ==========================================
        // MANAGE MEMBERS
        // ==========================================
        btnManageMembers.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AdminMembersActivity::class.java
                )
            )
        }


        // ==========================================
        // MANAGE EVENTS
        // ==========================================
        btnManageEvents.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AdminEventsActivity::class.java
                )
            )
        }


        // ==========================================
        // CREATE EVENT
        // ==========================================
        btnCreateEvent.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CreateEventActivity::class.java
                )
            )
        }


        // ==========================================
        // NOTIFICATIONS
        // ==========================================
        btnNotifications.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AdminNotificationsActivity::class.java
                )
            )
        }


        // ==========================================
        // PROFILE
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
        // SETTINGS
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


    override fun onResume() {
        super.onResume()

        if (!isFinishing) {
            loadAdminHome()
        }
    }
}