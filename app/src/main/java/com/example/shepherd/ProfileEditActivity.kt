package com.example.shepherd

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity

class ProfileEditActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        // -------------------------
        // BACK BUTTON
        // -------------------------

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }


        // -------------------------
        // INPUT FIELDS
        // -------------------------

        val etFirstName =
            findViewById<EditText>(R.id.etFirstName)

        val etLastName =
            findViewById<EditText>(R.id.etLastName)

        val etEmail =
            findViewById<EditText>(R.id.etEmail)

        val etPhone =
            findViewById<EditText>(R.id.etPhone)

        val etChurchRole =
            findViewById<EditText>(R.id.etChurchRole)


        // -------------------------
        // SAVE BUTTON
        // -------------------------

        val btnSave =
            findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {

            val firstName =
                etFirstName.text.toString().trim()

            val lastName =
                etLastName.text.toString().trim()

            val email =
                etEmail.text.toString().trim()

            val phone =
                etPhone.text.toString().trim()

            val churchRole =
                etChurchRole.text.toString().trim()


            // BASIC VALIDATION

            if (firstName.isEmpty()) {
                etFirstName.error = "Please enter your first name"
                etFirstName.requestFocus()
                return@setOnClickListener
            }

            if (lastName.isEmpty()) {
                etLastName.error = "Please enter your last name"
                etLastName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Please enter your email"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                etPhone.error = "Please enter your phone number"
                etPhone.requestFocus()
                return@setOnClickListener
            }


            // TEMPORARY SAVE

            Toast.makeText(
                this,
                "Profile updated successfully!",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}