package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

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

                    checkAndRedirectUser(userId, isGoogleSignIn = false) {
                        btnSignIn.isEnabled = true
                    }
                }
        }

        btnGoogleSignIn.setOnClickListener {
            btnGoogleSignIn.isEnabled = false
            launchGoogleSignIn {
                btnGoogleSignIn.isEnabled = true
            }
        }

        tvForgotPassword.setOnClickListener {

            Toast.makeText(
                this,
                "Password recovery will be added later.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun launchGoogleSignIn(onFinished: () -> Unit) {
        val credentialManager = CredentialManager.create(this)

        val serverClientId = getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleGoogleSignInResult(result.credential, onFinished)
            } catch (e: GetCredentialException) {
                onFinished()
                Toast.makeText(
                    this@LoginActivity,
                    "Google Sign-In failed: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                onFinished()
                Toast.makeText(
                    this@LoginActivity,
                    "Google Sign-In error: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleGoogleSignInResult(
        credential: androidx.credentials.Credential,
        onFinished: () -> Unit
    ) {
        if (credential is CustomCredential &&
            credential.type == "com.google.android.libraries.identity.googleid.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL"
        ) {
            try {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential =
                    GoogleAuthProvider.getCredential(googleIdToken, null)

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (!task.isSuccessful) {
                            onFinished()
                            Toast.makeText(
                                this,
                                "Firebase Google Auth failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            return@addOnCompleteListener
                        }

                        val firebaseUser = auth.currentUser
                        if (firebaseUser == null) {
                            onFinished()
                            Toast.makeText(
                                this,
                                "Unable to load your Google account.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@addOnCompleteListener
                        }

                        val userId = firebaseUser.uid
                        checkAndRedirectUser(userId, isGoogleSignIn = true) {
                            onFinished()
                        }
                    }
            } catch (e: GoogleIdTokenParsingException) {
                onFinished()
                Toast.makeText(
                    this,
                    "Failed to parse Google credential: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            onFinished()
            Toast.makeText(
                this,
                "Unsupported credential type.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkAndRedirectUser(
        userId: String,
        isGoogleSignIn: Boolean,
        onError: () -> Unit
    ) {
        firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    if (isGoogleSignIn) {
                        // Auto-provision a default profile for new Google sign-in users
                        val firebaseUser = auth.currentUser
                        val email = firebaseUser?.email ?: ""
                        val displayName = firebaseUser?.displayName ?: "Member"
                        val nameParts = displayName.split(" ", limit = 2)
                        val firstName = nameParts.getOrNull(0) ?: "Member"
                        val lastName = nameParts.getOrNull(1) ?: ""

                        val userMap = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "phone" to "",
                            "role" to "member",
                            "language" to "en",
                            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )

                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                LanguageHelper.setLanguage(this, "en")
                                saveFcmToken(userId) {
                                    redirectUserByRole("member")
                                }
                            }
                            .addOnFailureListener { exception ->
                                onError()
                                Toast.makeText(
                                    this,
                                    "Failed to create profile: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        onError()
                        Toast.makeText(
                            this,
                            "Your account profile could not be found.",
                            Toast.LENGTH_LONG
                        ).show()
                        auth.signOut()
                    }
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
                    redirectUserByRole(role)
                }
            }
            .addOnFailureListener { exception ->
                onError()
                Toast.makeText(
                    this,
                    "Could not load your profile: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun redirectUserByRole(role: String) {
        when (role) {
            "admin" -> {
                Toast.makeText(
                    this,
                    "Welcome, Admin!",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        AdminHomeActivity::class.java
                    )
                )
                finish()
            }

            "pastor" -> {
                Toast.makeText(
                    this,
                    "Welcome, Pastor!",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        PastorHomeActivity::class.java
                    )
                )
                finish()
            }

            else -> {
                Toast.makeText(
                    this,
                    "Welcome!",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        MemberHomeActivity::class.java
                    )
                )
                finish()
            }
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