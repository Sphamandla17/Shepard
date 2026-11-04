package com.example.shepherd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shepherd.api.Announcement
import com.example.shepherd.api.AnnouncementAdapter
import com.example.shepherd.api.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class AnnouncementsActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var announcementAdapter: AnnouncementAdapter
    private val PREF_NAME = "ShepherdAnnouncementsCache"
    private val KEY_ANNOUNCEMENTS = "cached_announcements"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(
            R.layout.activity_announcements
        )

        recyclerView =
            findViewById(
                R.id.rvAnnouncements
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        announcementAdapter =
            AnnouncementAdapter(
                emptyList()
            ) { announcement ->
                val intent = Intent(this, AnnouncementDetailsActivity::class.java).apply {
                    putExtra("title", announcement.title)
                    putExtra("message", announcement.message)
                    putExtra("createdAt", announcement.createdAt)
                    putExtra("createdBy", announcement.createdBy)
                    putExtra("imageUrl", announcement.imageUrl)
                }
                startActivity(intent)
            }

        recyclerView.adapter =
            announcementAdapter

        val btnBack =
            findViewById<ImageButton>(
                R.id.btnBack
            )

        btnBack.setOnClickListener {
            finish()
        }

        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        val isOnline = NetworkUtils.isInternetAvailable(this)

        if (!isOnline) {
            loadFromCache("Displaying cached announcements (offline)")
            return
        }

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .apiService
                        .getAnnouncements()

                if (response.isSuccessful) {

                    val announcements =
                        response.body()
                            ?: emptyList()

                    saveToCache(announcements)
                    announcementAdapter
                        .updateAnnouncements(
                            announcements
                        )

                } else {

                    loadFromCache("Failed to load from server. Displaying cached announcements.")
                }

            } catch (e: Exception) {

                loadFromCache("Unable to connect to server. Displaying cached announcements.")
            }
        }
    }

    private fun saveToCache(announcements: List<Announcement>) {
        try {
            val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = Gson().toJson(announcements)
            prefs.edit().putString(KEY_ANNOUNCEMENTS, json).apply()
        } catch (e: Exception) {
            // Ignore cache write errors
        }
    }

    private fun loadFromCache(feedbackMessage: String) {
        try {
            val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_ANNOUNCEMENTS, null)
            if (!json.isNullOrEmpty()) {
                val type = object : TypeToken<List<Announcement>>() {}.type
                val cachedList: List<Announcement> = Gson().fromJson(json, type) ?: emptyList()
                if (cachedList.isNotEmpty()) {
                    announcementAdapter.updateAnnouncements(cachedList)
                    Toast.makeText(this, feedbackMessage, Toast.LENGTH_LONG).show()
                    return
                }
            }
            Toast.makeText(this, "No internet connection and no cached announcements available", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load cached announcements", Toast.LENGTH_LONG).show()
        }
    }
}
