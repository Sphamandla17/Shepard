package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        // ==========================================
        // FIREBASE
        // ==========================================

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // ==========================================
        // CONNECT UI ELEMENTS
        // ==========================================

        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        val etFirstName =
            findViewById<EditText>(R.id.etFirstName)

        val etLastName =
            findViewById<EditText>(R.id.etLastName)

        val etEmail =
            findViewById<EditText>(R.id.etEmail)

        val etPhone =
            findViewById<EditText>(R.id.etPhone)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val etConfirmPassword =
            findViewById<EditText>(R.id.etConfirmPassword)

        val btnCreateAccount =
            findViewById<Button>(R.id.btnCreateAccount)

        val tvSignIn =
            findViewById<TextView>(R.id.tvSignIn)

        // ==========================================
        // BACK BUTTON
        // ==========================================

        btnBack.setOnClickListener {
            finish()
        }

        // ==========================================
        // ALREADY HAVE AN ACCOUNT
        // ==========================================

        tvSignIn.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)
        }

        // ==========================================
        // CREATE ACCOUNT
        // ==========================================

        btnCreateAccount.setOnClickListener {

            // Get information from the form

            val firstName =
                etFirstName.text.toString().trim()

            val lastName =
                etLastName.text.toString().trim()

            val email =
                etEmail.text.toString().trim()

            val phone =
                etPhone.text.toString().trim()

            val password =
                etPassword.text.toString()

            val confirmPassword =
                etConfirmPassword.text.toString()

            // ==========================================
            // VALIDATION
            // ==========================================

            if (firstName.isEmpty()) {

                etFirstName.error =
                    "Please enter your first name"

                etFirstName.requestFocus()

                return@setOnClickListener
            }

            if (lastName.isEmpty()) {

                etLastName.error =
                    "Please enter your last name"

                etLastName.requestFocus()

                return@setOnClickListener
            }

            if (email.isEmpty()) {

                etEmail.error =
                    "Please enter your email address"

                etEmail.requestFocus()

                return@setOnClickListener
            }

            if (phone.isEmpty()) {

                etPhone.error =
                    "Please enter your phone number"

                etPhone.requestFocus()

                return@setOnClickListener
            }

            if (password.isEmpty()) {

                etPassword.error =
                    "Please enter a password"

                etPassword.requestFocus()

                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {

                etConfirmPassword.error =
                    "Please confirm your password"

                etConfirmPassword.requestFocus()

                return@setOnClickListener
            }

            if (password.length < 8) {

                etPassword.error =
                    "Password must be at least 8 characters"

                etPassword.requestFocus()

                return@setOnClickListener
            }

            if (password != confirmPassword) {

                etConfirmPassword.error =
                    "Passwords do not match"

                etConfirmPassword.requestFocus()

                return@setOnClickListener
            }

            // ==========================================
            // CREATE FIREBASE AUTHENTICATION ACCOUNT
            // ==========================================

            auth.createUserWithEmailAndPassword(
                email,
                password
            )
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        // Get the newly created Firebase user

                        val firebaseUser =
                            auth.currentUser

                        if (firebaseUser == null) {

                            Toast.makeText(
                                this,
                                "Account created, but user information could not be loaded.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addOnCompleteListener
                        }

                        // Firebase Authentication User ID

                        val userId =
                            firebaseUser.uid

                        // ==========================================
                        // GET SELECTED LANGUAGE
                        // ==========================================

                        val selectedLanguage =
                            LanguageHelper.getSavedLanguage(this)

                        val language =
                            if (selectedLanguage.isEmpty()) {
                                "en"
                            } else {
                                selectedLanguage
                            }

                        // ==========================================
                        // CREATE USER PROFILE
                        // ==========================================

                        val userProfile = hashMapOf(

                            "userId" to userId,

                            "firstName" to firstName,

                            "lastName" to lastName,

                            "email" to email,

                            "phone" to phone,

                            "role" to "member",

                            "language" to language,

                            "createdAt" to
                                    com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )

                        // ==========================================
                        // SAVE PROFILE TO FIRESTORE
                        // ==========================================

                        firestore
                            .collection("users")
                            .document(userId)
                            .set(userProfile)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    "Account created successfully!",
                                    Toast.LENGTH_LONG
                                ).show()

                                // Go to Login

                                val intent = Intent(
                                    this,
                                    LoginActivity::class.java
                                )

                                startActivity(intent)

                                finish()
                            }
                            .addOnFailureListener { exception ->

                                Toast.makeText(
                                    this,
                                    "Account created, but profile could not be saved: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                    } else {

                        // ==========================================
                        // REGISTRATION FAILED
                        // ==========================================

                        val errorMessage =
                            task.exception?.message
                                ?: "Registration failed"

                        Toast.makeText(
                            this,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}