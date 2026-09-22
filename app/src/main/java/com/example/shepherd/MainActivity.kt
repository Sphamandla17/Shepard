package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private val splashDelay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({

            val savedLanguage = LanguageHelper.getSavedLanguage(this)

            if (savedLanguage.isEmpty()) {

                // No language has been selected yet
                val intent = Intent(
                    this@MainActivity,
                    LanguageSelectionActivity::class.java
                )

                startActivity(intent)

            } else {

                // A language was already selected
                LanguageHelper.setLanguage(
                    this,
                    savedLanguage
                )

                val intent = Intent(
                    this@MainActivity,
                    WelcomeActivity::class.java
                )

                startActivity(intent)
            }

            finish()

        }, splashDelay)
    }
}