package com.example.shepherd

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PrayerRequestFormActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_prayer_request_form)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val etPrayerRequest = findViewById<EditText>(R.id.etPrayerRequest)
        val switchShareLocation =
            findViewById<Switch>(R.id.switchShareLocation)
        val btnSubmitPrayer =
            findViewById<Button>(R.id.btnSubmitPrayer)

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Prayer categories
        val categories = arrayOf(
            "General",
            "Family",
            "Health",
            "Work/Education",
            "Financial",
            "Spiritual",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategory.adapter = adapter

        // Submit prayer request
        btnSubmitPrayer.setOnClickListener {

            val prayerText =
                etPrayerRequest.text.toString().trim()

            if (prayerText.isEmpty()) {
                etPrayerRequest.error =
                    "Please enter your prayer request"
                return@setOnClickListener
            }

            val currentUser = auth.currentUser

            if (currentUser == null) {
                Toast.makeText(
                    this,
                    "Please log in first",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val category =
                spinnerCategory.selectedItem.toString()

            val locationShared =
                switchShareLocation.isChecked

            val prayerRequest = hashMapOf(
                "userId" to currentUser.uid,
                "category" to category,
                "requestText" to prayerText,
                "status" to "Received",
                "pastorFeedback" to "",
                "locationShared" to locationShared,
                "createdAt" to FieldValue.serverTimestamp()
            )

            btnSubmitPrayer.isEnabled = false

            firestore.collection("prayerRequests")
                .add(prayerRequest)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Prayer request submitted successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
                .addOnFailureListener {

                    btnSubmitPrayer.isEnabled = true

                    Toast.makeText(
                        this,
                        "Failed to submit prayer request",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}