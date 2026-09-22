package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // PROFILE TEXT VIEWS

        val tvFirstName =
            findViewById<TextView>(R.id.tvFirstName)

        val tvLastName =
            findViewById<TextView>(R.id.tvLastName)

        val tvEmail =
            findViewById<TextView>(R.id.tvEmail)

        val tvPhone =
            findViewById<TextView>(R.id.tvPhone)

        val tvRole =
            findViewById<TextView>(R.id.tvRole)

        val tvLanguage =
            findViewById<TextView>(R.id.tvLanguage)

        // BUTTONS / ROWS

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        val rowSettings =
            findViewById<LinearLayout>(R.id.rowSettings)

        val rowLogout =
            findViewById<LinearLayout>(R.id.rowLogout)

        // BACK BUTTON

        btnBack.setOnClickListener {
            finish()
        }

        // SETTINGS

        rowSettings.setOnClickListener {
            val intent = Intent(
                this,
                SettingsActivity::class.java
            )

            startActivity(intent)
        }

        // LOG OUT

        rowLogout.setOnClickListener {

            auth.signOut()

            Toast.makeText(
                this,
                "You have been logged out.",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }

        // GET CURRENT USER

        val currentUser = auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in again.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        val userId = currentUser.uid

        // LOAD PROFILE FROM FIRESTORE

        firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    tvFirstName.text =
                        document.getString("firstName") ?: "-"

                    tvLastName.text =
                        document.getString("lastName") ?: "-"

                    tvEmail.text =
                        document.getString("email") ?: "-"

                    tvPhone.text =
                        document.getString("phone") ?: "-"

                    tvRole.text =
                        document.getString("role") ?: "member"

                    val language =
                        document.getString("language") ?: "en"

                    tvLanguage.text =
                        if (language == "zu") {
                            "isiZulu"
                        } else {
                            "English"
                        }

                } else {

                    Toast.makeText(
                        this,
                        "Profile information could not be found.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Could not load profile: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}