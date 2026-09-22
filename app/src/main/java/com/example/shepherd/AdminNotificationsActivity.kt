package com.example.shepherd

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdminNotificationsActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_notifications)

        firestore = FirebaseFirestore.getInstance()

        val etEnglishTitle =
            findViewById<EditText>(R.id.etEnglishTitle)

        val etEnglishMessage =
            findViewById<EditText>(R.id.etEnglishMessage)

        val etZuluTitle =
            findViewById<EditText>(R.id.etZuluTitle)

        val etZuluMessage =
            findViewById<EditText>(R.id.etZuluMessage)

        val btnSendNotification =
            findViewById<Button>(R.id.btnSendNotification)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        btnSendNotification.setOnClickListener {

            val englishTitle =
                etEnglishTitle.text.toString().trim()

            val englishMessage =
                etEnglishMessage.text.toString().trim()

            val zuluTitle =
                etZuluTitle.text.toString().trim()

            val zuluMessage =
                etZuluMessage.text.toString().trim()

            if (englishTitle.isEmpty()) {
                etEnglishTitle.error =
                    "Please enter the English notification title"
                etEnglishTitle.requestFocus()
                return@setOnClickListener
            }

            if (englishMessage.isEmpty()) {
                etEnglishMessage.error =
                    "Please enter the English notification message"
                etEnglishMessage.requestFocus()
                return@setOnClickListener
            }

            if (zuluTitle.isEmpty()) {
                etZuluTitle.error =
                    "Please enter the isiZulu notification title"
                etZuluTitle.requestFocus()
                return@setOnClickListener
            }

            if (zuluMessage.isEmpty()) {
                etZuluMessage.error =
                    "Please enter the isiZulu notification message"
                etZuluMessage.requestFocus()
                return@setOnClickListener
            }

            btnSendNotification.isEnabled = false

            val notification = hashMapOf(
                "englishTitle" to englishTitle,
                "englishMessage" to englishMessage,
                "zuluTitle" to zuluTitle,
                "zuluMessage" to zuluMessage,
                "createdAt" to FieldValue.serverTimestamp()
            )

            firestore
                .collection("notifications")
                .add(notification)
                .addOnSuccessListener { documentReference ->

                    btnSendNotification.isEnabled = true

                    Toast.makeText(
                        this,
                        "NOTIFICATION SAVED TO FIRESTORE",
                        Toast.LENGTH_LONG
                    ).show()

                    etEnglishTitle.text.clear()
                    etEnglishMessage.text.clear()
                    etZuluTitle.text.clear()
                    etZuluMessage.text.clear()
                }
                .addOnFailureListener { exception ->

                    btnSendNotification.isEnabled = true

                    Toast.makeText(
                        this,
                        "FIRESTORE ERROR: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}