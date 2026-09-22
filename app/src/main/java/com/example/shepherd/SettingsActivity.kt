package com.example.shepherd

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved language before loading the layout
        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // ==========================================
        // BACK BUTTON
        // ==========================================

        val btnBack =
            findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        // ==========================================
        // LANGUAGE VIEWS
        // ==========================================

        val btnChangeLanguage =
            findViewById<LinearLayout>(R.id.btnChangeLanguage)

        val tvCurrentLanguage =
            findViewById<TextView>(R.id.tvCurrentLanguage)

        updateCurrentLanguageText(tvCurrentLanguage)

        // ==========================================
        // CHANGE LANGUAGE
        // ==========================================

        btnChangeLanguage.setOnClickListener {

            val languages = arrayOf(
                getString(R.string.english),
                getString(R.string.isizulu)
            )

            val currentLanguage =
                LanguageHelper.getSavedLanguage(this)

            val selectedIndex =
                if (currentLanguage == "zu") {
                    1
                } else {
                    0
                }

            AlertDialog.Builder(this)
                .setTitle(
                    getString(R.string.choose_language_dialog)
                )
                .setSingleChoiceItems(
                    languages,
                    selectedIndex
                ) { dialog, which ->

                    val selectedLanguage =
                        if (which == 1) {
                            "zu"
                        } else {
                            "en"
                        }

                    // ==========================================
                    // SAVE LANGUAGE LOCALLY
                    // ==========================================

                    LanguageHelper.saveLanguage(
                        this,
                        selectedLanguage
                    )

                    // ==========================================
                    // APPLY LANGUAGE
                    // ==========================================

                    LanguageHelper.setLanguage(
                        this,
                        selectedLanguage
                    )

                    dialog.dismiss()

                    // ==========================================
                    // RECREATE SETTINGS SCREEN
                    // ==========================================

                    recreate()

                    // ==========================================
                    // SAVE LANGUAGE TO FIRESTORE
                    // ==========================================

                    val currentUser =
                        auth.currentUser

                    if (currentUser != null) {

                        firestore
                            .collection("users")
                            .document(currentUser.uid)
                            .update(
                                "language",
                                selectedLanguage
                            )
                            .addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    getString(
                                        R.string.language_updated
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { exception ->

                                Toast.makeText(
                                    this,
                                    getString(
                                        R.string.language_saved_profile_failed,
                                        exception.message ?: ""
                                    ),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

// ==========================================
// UPDATE CURRENT LANGUAGE TEXT
// ==========================================

    private fun updateCurrentLanguageText(
        textView: TextView
    ) {

        val savedLanguage =
            LanguageHelper.getSavedLanguage(this)

        textView.text =
            if (savedLanguage == "zu") {
                getString(R.string.isizulu)
            } else {
                getString(R.string.english)
            }
    }


}
