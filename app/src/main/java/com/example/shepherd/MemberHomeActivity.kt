package com.example.shepherd

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemberHomeActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var currentLanguage = ""
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var tvOfflineBanner: TextView? = null
    private var hideRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLanguage =
            LanguageHelper.getSavedLanguage(this)

        setContentView(R.layout.activity_member_home)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tvOfflineBanner =
            findViewById(R.id.tvOfflineBanner)

        val tvWelcomeMessage =
            findViewById<TextView>(R.id.tvWelcomeMessage)

        val btnPrayerRequests =
            findViewById<Button>(R.id.btnPrayerRequests)

        val btnEvents =
            findViewById<Button>(R.id.btnEvents)

        val btnBible =
            findViewById<Button>(R.id.btnBible)

        val btnAnnouncements =
            findViewById<Button>(R.id.btnAnnouncements)

        val btnNotifications =
            findViewById<Button>(R.id.btnNotifications)



        val btnProfile =
            findViewById<Button>(R.id.btnProfile)

        val btnSettings =
            findViewById<Button>(R.id.btnSettings)

        val currentUser =
            auth.currentUser

        if (currentUser != null) {

            val userId =
                currentUser.uid

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val firstName =
                            document.getString("firstName")
                                ?: "Member"

                        tvWelcomeMessage.text =
                            if (
                                LanguageHelper.getSavedLanguage(this)
                                == "zu"
                            ) {
                                "Wamukelekile, $firstName"
                            } else {
                                "Welcome, $firstName"
                            }
                    }
                }
        }

        // PRAYER REQUESTS

        btnPrayerRequests.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PrayerRequestsActivity::class.java
                )
            )
        }

        // EVENTS

        btnEvents.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EventsActivity::class.java
                )
            )
        }

        // BIBLE

        btnBible.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    BibleActivity::class.java
                )
            )
        }

        // ANNOUNCEMENTS

        btnAnnouncements.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AnnouncementsActivity::class.java
                )
            )
        }

        // NOTIFICATIONS

        btnNotifications.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    NotificationsActivity::class.java
                )
            )
        }



        // PROFILE

        btnProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // SETTINGS

        btnSettings.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val initialAvailable = NetworkUtils.isInternetAvailable(this)
        Log.d("SHEPHERD_OFFLINE", "onStart: initial isInternetAvailable = $initialAvailable")
        updateConnectionStatus(initialAvailable)

        if (networkCallback == null) {
            networkCallback = NetworkUtils.registerNetworkCallback(
                context = this,
                onAvailable = {
                    Log.d("SHEPHERD_OFFLINE", "Callback triggered: onAvailable")
                },
                onValidated = {
                    Log.d("SHEPHERD_OFFLINE", "Callback triggered: onValidated")
                    runOnUiThread {
                        val avail = NetworkUtils.isInternetAvailable(this)
                        Log.d("SHEPHERD_OFFLINE", "onValidated check: isInternetAvailable = $avail")
                        if (avail) {
                            updateConnectionStatus(true)
                        }
                    }
                },
                onInternetLost = {
                    Log.d("SHEPHERD_OFFLINE", "Callback triggered: onInternetLost")
                    runOnUiThread {
                        val avail = NetworkUtils.isInternetAvailable(this)
                        Log.d("SHEPHERD_OFFLINE", "onInternetLost check: isInternetAvailable = $avail")
                        if (!avail) {
                            updateConnectionStatus(false)
                        }
                    }
                },
                onLost = {
                    Log.d("SHEPHERD_OFFLINE", "Callback triggered: onLost")
                    runOnUiThread {
                        val avail = NetworkUtils.isInternetAvailable(this)
                        Log.d("SHEPHERD_OFFLINE", "onLost check: isInternetAvailable = $avail")
                        if (!avail) {
                            updateConnectionStatus(false)
                        }
                    }
                }
            )
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("SHEPHERD_OFFLINE", "onStop: unregistering networkCallback")
        hideRunnable?.let { tvOfflineBanner?.removeCallbacks(it) }
        hideRunnable = null
        NetworkUtils.unregisterNetworkCallback(this, networkCallback)
        networkCallback = null
    }

    private fun updateConnectionStatus(isAvailable: Boolean) {
        Log.d("SHEPHERD_OFFLINE", "updateConnectionStatus called with: isAvailable = $isAvailable")
        tvOfflineBanner?.let { banner ->
            // Cancel any pending delayed hide runnables immediately when state changes
            hideRunnable?.let { banner.removeCallbacks(it) }
            hideRunnable = null

            if (isAvailable) {
                if (NetworkUtils.isInternetAvailable(this)) {
                    banner.text = "Back online"
                    banner.visibility = View.VISIBLE
                    Log.d("SHEPHERD_OFFLINE", "Banner assigned visibility: View.VISIBLE (Back online)")

                    val runnable = Runnable {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            banner.visibility = View.GONE
                            Log.d("SHEPHERD_OFFLINE", "Banner assigned visibility: View.GONE (Hidden after delay)")
                        }
                    }
                    hideRunnable = runnable
                    banner.postDelayed(runnable, 3000)
                } else {
                    banner.text = "No internet connection – using cached data"
                    banner.visibility = View.VISIBLE
                    Log.d("SHEPHERD_OFFLINE", "Banner assigned visibility: View.VISIBLE (Offline fallback)")
                }
            } else {
                banner.text = "No internet connection – using cached data"
                banner.visibility = View.VISIBLE
                Log.d("SHEPHERD_OFFLINE", "Banner assigned visibility: View.VISIBLE (Offline)")
            }
        } ?: Log.d("SHEPHERD_OFFLINE", "tvOfflineBanner is NULL!")
    }

    override fun onResume() {
        super.onResume()

        val savedLanguage =
            LanguageHelper.getSavedLanguage(this)

        if (
            currentLanguage.isNotEmpty() &&
            savedLanguage.isNotEmpty() &&
            currentLanguage != savedLanguage
        ) {
            recreate()
        }
    }
}
