package com.example.shepherd

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EventRegistrationActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var eventId = ""

    private lateinit var tvEventName: TextView
    private lateinit var tvEventDateTime: TextView
    private lateinit var tvEventLocation: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_event_registration)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        val btnConfirmRegistration =
            findViewById<Button>(R.id.btnConfirmRegistration)

        tvEventName =
            findViewById(R.id.tvEventName)

        tvEventDateTime =
            findViewById(R.id.tvEventDateTime)

        tvEventLocation =
            findViewById(R.id.tvEventLocation)

        tvUserName =
            findViewById(R.id.tvUserName)

        tvUserEmail =
            findViewById(R.id.tvUserEmail)

        // GET SELECTED EVENT

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

        // LOAD EVENT AND USER

        loadEvent()

        loadUser()

        // CONFIRM REGISTRATION

        btnConfirmRegistration.setOnClickListener {

            registerForEvent(
                btnConfirmRegistration
            )
        }
    }

    // ============================
    // LOAD EVENT
    // ============================

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
                    document.getString("title")
                        ?: "Event"

                val date =
                    document.getString("date")
                        ?: ""

                val time =
                    document.getString("time")
                        ?: ""

                val location =
                    document.getString("location")
                        ?: ""

                tvEventName.text =
                    title

                tvEventDateTime.text =
                    "$date • $time"

                tvEventLocation.text =
                    location
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to load event",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // ============================
    // LOAD USER
    // ============================

    private fun loadUser() {

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

        val userId =
            currentUser.uid

        tvUserEmail.text =
            currentUser.email
                ?: "No email available"

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name =
                        document.getString("name")

                    if (!name.isNullOrEmpty()) {

                        tvUserName.text =
                            name

                    } else {

                        tvUserName.text =
                            "Member"
                    }

                } else {

                    tvUserName.text =
                        "Member"
                }
            }
            .addOnFailureListener {

                tvUserName.text =
                    "Member"
            }
    }

    // ============================
    // REGISTER FOR EVENT
    // ============================

    private fun registerForEvent(
        button: Button
    ) {

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

        button.isEnabled = false

        val userId =
            currentUser.uid

        /*
         * Each registration gets a unique ID
         * based on the event and user.
         *
         * This prevents duplicate registrations.
         */

        val registrationId =
            "${eventId}_${userId}"

        val registration =
            hashMapOf<String, Any>(
                "eventId" to eventId,
                "userId" to userId,
                "registeredAt" to FieldValue.serverTimestamp()
            )

        firestore.collection("eventRegistrations")
            .document(registrationId)
            .set(registration)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Registration confirmed!",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
            .addOnFailureListener {

                button.isEnabled = true

                Toast.makeText(
                    this,
                    "Registration failed. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}