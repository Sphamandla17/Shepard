package com.example.shepherd

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private val splashDelay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({

            val savedLanguage = LanguageHelper.getSavedLanguage(this)

            if (savedLanguage.isEmpty()) {
                val intent = Intent(
                    this@MainActivity,
                    LanguageSelectionActivity::class.java
                )
                startActivity(intent)
                finish()
                return@postDelayed
            }

            LanguageHelper.setLanguage(this, savedLanguage)

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                ShepherdFirebaseMessagingService.updateTokenForCurrentUser()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
                    }
                }

                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users").document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role")?.lowercase() ?: "member"
                        val intent = when (role) {
                            "admin" -> Intent(this, AdminHomeActivity::class.java)
                            "pastor" -> Intent(this, PastorHomeActivity::class.java)
                            else -> Intent(this, MemberHomeActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
            } else {
                val intent = Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )
                startActivity(intent)
                finish()
            }

        }, splashDelay)
    }
}
