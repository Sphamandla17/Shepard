package com.example.shepherd

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shepherd.api.AnnouncementAdapter
import com.example.shepherd.api.RetrofitClient
import kotlinx.coroutines.launch

class AnnouncementsActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var announcementAdapter: AnnouncementAdapter

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
            )

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

                    announcementAdapter
                        .updateAnnouncements(
                            announcements
                        )

                } else {

                    Toast.makeText(
                        this@AnnouncementsActivity,
                        "Failed to load announcements",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@AnnouncementsActivity,
                    "Unable to connect to the server",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}