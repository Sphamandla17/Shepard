package com.example.shepherd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.ComponentActivity

class LanguageSelectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_language_selection)

        val rbEnglish = findViewById<RadioButton>(R.id.rbEnglish)
        val rbIsiZulu = findViewById<RadioButton>(R.id.rbIsiZulu)

        val cvEnglish = findViewById<LinearLayout>(R.id.cvEnglish)
        val cvIsiZulu = findViewById<LinearLayout>(R.id.cvIsiZulu)

        val btnContinue = findViewById<Button>(R.id.btnContinue)

        rbEnglish.isChecked = true
        rbIsiZulu.isChecked = false

        cvEnglish.setOnClickListener {
            rbEnglish.isChecked = true
            rbIsiZulu.isChecked = false
        }

        cvIsiZulu.setOnClickListener {
            rbEnglish.isChecked = false
            rbIsiZulu.isChecked = true
        }

        btnContinue.setOnClickListener {

            val language = if (rbIsiZulu.isChecked) {
                "zu"
            } else {
                "en"
            }

            // Save language
            LanguageHelper.saveLanguage(this, language)

            // Apply language
            LanguageHelper.setLanguage(this, language)

            // Go to Welcome
            val intent = Intent(
                this,
                WelcomeActivity::class.java
            )

            startActivity(intent)
            finish()
        }
    }
}