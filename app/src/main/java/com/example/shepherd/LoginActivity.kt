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
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        val etEmail =
            findViewById<EditText>(R.id.etEmail)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val btnSignIn =
            findViewById<Button>(R.id.btnSignIn)

        val btnGoogleSignIn =
            findViewById<Button>(R.id.btnGoogleSignIn)

        val tvForgotPassword =
            findViewById<TextView>(R.id.tvForgotPassword)

        val tvCreateAccount =
            findViewById<TextView>(R.id.tvCreateAccount)

        btnBack.setOnClickListener {
            finish()
        }

        tvCreateAccount.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }

        btnSignIn.setOnClickListener {

            val email =
                etEmail.text.toString().trim()

            val password =
                etPassword.text.toString()

            if (email.isEmpty()) {

                etEmail.error =
                    "Please enter your email address"

                etEmail.requestFocus()

                return@setOnClickListener
            }

            if (password.isEmpty()) {

                etPassword.error =
                    "Please enter your password"

                etPassword.requestFocus()

                return@setOnClickListener
            }

            btnSignIn.isEnabled = false

            auth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnCompleteListener { task ->

                    if (!task.isSuccessful) {

                        btnSignIn.isEnabled = true

                        val errorMessage =
                            task.exception?.message
                                ?: "Login failed"

                        Toast.makeText(
                            this,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()

                        return@addOnCompleteListener
                    }

                    val firebaseUser =
                        auth.currentUser

                    if (firebaseUser == null) {

                        btnSignIn.isEnabled = true

                        Toast.makeText(
                            this,
                            "Unable to load your account.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@addOnCompleteListener
                    }

                    val userId =
                        firebaseUser.uid

                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { document ->

                            btnSignIn.isEnabled = true

                            if (!document.exists()) {

                                Toast.makeText(
                                    this,
                                    "Your account profile could not be found.",
                                    Toast.LENGTH_LONG
                                ).show()

                                auth.signOut()

                                return@addOnSuccessListener
                            }

                            val role =
                                document.getString("role")
                                    ?.lowercase()
                                    ?: "member"

                            val language =
                                document.getString("language")
                                    ?: "en"

                            LanguageHelper.setLanguage(
                                this,
                                language
                            )

                            saveFcmToken(userId) {

                                when (role) {

                                    "admin" -> {

                                        Toast.makeText(
                                            this,
                                            "Welcome, Admin!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(
                                                this,
                                                AdminHomeActivity::class.java
                                            )

                                        startActivity(intent)
                                        finish()
                                    }

                                    "pastor" -> {

                                        Toast.makeText(
                                            this,
                                            "Welcome, Pastor!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(
                                                this,
                                                PastorHomeActivity::class.java
                                            )

                                        startActivity(intent)
                                        finish()
                                    }

                                    else -> {

                                        Toast.makeText(
                                            this,
                                            "Welcome!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(
                                                this,
                                                MemberHomeActivity::class.java
                                            )

                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { exception ->

                            btnSignIn.isEnabled = true

                            Toast.makeText(
                                this,
                                "Could not load your profile: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
        }

        btnGoogleSignIn.setOnClickListener {

            Toast.makeText(
                this,
                "Google Sign-In will be added later.",
                Toast.LENGTH_SHORT
            ).show()
        }

        tvForgotPassword.setOnClickListener {

            Toast.makeText(
                this,
                "Password recovery will be added later.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveFcmToken(
        userId: String,
        onComplete: () -> Unit
    ) {

        FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {

                    onComplete()

                    return@addOnCompleteListener
                }

                val token =
                    task.result

                firestore
                    .collection("users")
                    .document(userId)
                    .update(
                        "fcmToken",
                        token
                    )
                    .addOnCompleteListener {

                        onComplete()
                    }
            }
    }
}