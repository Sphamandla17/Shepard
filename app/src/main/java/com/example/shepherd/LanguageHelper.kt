package com.example.shepherd

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageHelper {


    private const val PREFS_NAME = "ShepherdPreferences"
    private const val LANGUAGE_KEY = "selected_language"

// ==========================================
// SAVE SELECTED LANGUAGE
// ==========================================

    fun saveLanguage(context: Context, language: String) {

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        preferences.edit()
            .putString(LANGUAGE_KEY, language)
            .apply()
    }

// ==========================================
// GET SAVED LANGUAGE
// ==========================================

    fun getSavedLanguage(context: Context): String {

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return preferences.getString(
            LANGUAGE_KEY,
            "en"
        ) ?: "en"
    }

// ==========================================
// APPLY LANGUAGE
// ==========================================

    fun setLanguage(
        context: Context,
        language: String
    ) {

        val selectedLanguage =
            if (language == "zu") {
                "zu"
            } else {
                "en"
            }

        saveLanguage(
            context,
            selectedLanguage
        )

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(
                selectedLanguage
            )
        )
    }

}
