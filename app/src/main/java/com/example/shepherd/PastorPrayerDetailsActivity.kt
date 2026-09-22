package com.example.shepherd

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PastorPrayerDetailsActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore

    private var prayerRequestId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_pastor_prayer_details)

        firestore = FirebaseFirestore.getInstance()

        prayerRequestId =
            intent.getStringExtra("prayerRequestId") ?: ""

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        val btnMarkUnderPrayer =
            findViewById<Button>(R.id.btnMarkUnderPrayer)

        val btnMarkCompleted =
            findViewById<Button>(R.id.btnMarkCompleted)

        val btnSendFeedback =
            findViewById<Button>(R.id.btnSendFeedback)

        btnBack.setOnClickListener {
            finish()
        }

        if (prayerRequestId.isEmpty()) {

            Toast.makeText(
                this,
                "Prayer request could not be found",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        loadPrayerRequest()

        btnMarkUnderPrayer.setOnClickListener {
            updatePrayerStatus("Praying")
        }

        btnMarkCompleted.setOnClickListener {
            updatePrayerStatus("Completed")
        }

        btnSendFeedback.setOnClickListener {

            val etPastorFeedback =
                findViewById<EditText>(
                    R.id.etPastorFeedback
                )

            val feedback =
                etPastorFeedback.text.toString().trim()

            if (feedback.isEmpty()) {

                etPastorFeedback.error =
                    "Please enter feedback"

                return@setOnClickListener
            }

            sendFeedback(
                feedback,
                btnSendFeedback,
                etPastorFeedback
            )
        }
    }

    private fun loadPrayerRequest() {

        firestore
            .collection("prayerRequests")
            .document(prayerRequestId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    Toast.makeText(
                        this,
                        "Prayer request not found",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return@addOnSuccessListener
                }

                val tvMemberName =
                    findViewById<TextView>(
                        R.id.tvMemberName
                    )

                val tvDate =
                    findViewById<TextView>(
                        R.id.tvDate
                    )

                val tvPrayerRequestContent =
                    findViewById<TextView>(
                        R.id.tvPrayerRequestContent
                    )

                val tvStatus =
                    findViewById<TextView>(
                        R.id.tvStatus
                    )

                val etPastorFeedback =
                    findViewById<EditText>(
                        R.id.etPastorFeedback
                    )

                val userId =
                    document.getString("userId") ?: ""

                val category =
                    document.getString("category") ?: ""

                val requestText =
                    document.getString("requestText") ?: ""

                val status =
                    document.getString("status")
                        ?: "Received"

                val pastorFeedback =
                    document.getString("pastorFeedback")
                        ?: ""

                tvPrayerRequestContent.text =
                    requestText

                tvStatus.text =
                    status

                etPastorFeedback.setText(
                    pastorFeedback
                )

                if (category.isNotEmpty()) {

                    tvDate.text =
                        "Category: $category"
                }

                loadMemberName(
                    userId,
                    tvMemberName
                )
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to load prayer request",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun loadMemberName(
        userId: String,
        tvMemberName: TextView
    ) {

        if (userId.isEmpty()) {

            tvMemberName.text =
                "Prayer Request"

            return
        }

        firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val firstName =
                        document.getString("firstName")
                            ?: ""

                    val lastName =
                        document.getString("lastName")
                            ?: ""

                    val fullName =
                        "$firstName $lastName".trim()

                    tvMemberName.text =
                        if (fullName.isNotEmpty()) {
                            fullName
                        } else {
                            "Prayer Request"
                        }

                } else {

                    tvMemberName.text =
                        "Prayer Request"
                }
            }
            .addOnFailureListener {

                tvMemberName.text =
                    "Prayer Request"
            }
    }

    private fun updatePrayerStatus(
        newStatus: String
    ) {

        if (prayerRequestId.isEmpty()) {
            return
        }

        firestore
            .collection("prayerRequests")
            .document(prayerRequestId)
            .update(
                "status",
                newStatus
            )
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Prayer request marked as $newStatus",
                    Toast.LENGTH_SHORT
                ).show()

                findViewById<TextView>(
                    R.id.tvStatus
                ).text = newStatus
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to update prayer request",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun sendFeedback(
        feedback: String,
        button: Button,
        editText: EditText
    ) {

        if (prayerRequestId.isEmpty()) {
            return
        }

        button.isEnabled = false

        val updates = hashMapOf<String, Any>(
            "pastorFeedback" to feedback,
            "feedbackSentAt" to FieldValue.serverTimestamp()
        )

        firestore
            .collection("prayerRequests")
            .document(prayerRequestId)
            .update(updates)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Feedback sent successfully",
                    Toast.LENGTH_LONG
                ).show()

                button.isEnabled = true

                editText.setText("")
            }
            .addOnFailureListener {

                button.isEnabled = true

                Toast.makeText(
                    this,
                    "Failed to send feedback",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}