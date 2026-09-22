package com.example.shepherd

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminEventRegistrationsActivity : AppCompatActivity() {

    private lateinit var tvEventName: TextView
    private lateinit var etSearchAttendees: EditText
    private lateinit var rvAdminRegistrations: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    private lateinit var adapter: AdminRegistrationAdapter

    private val registrationList = mutableListOf<RegistrationMember>()
    private val filteredList = mutableListOf<RegistrationMember>()

    private var eventId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved language
        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_admin_event_registrations)

        // Get event ID
        eventId = intent.getStringExtra("eventId") ?: ""

        // Connect XML views
        tvEventName = findViewById(R.id.tvEventName)
        etSearchAttendees = findViewById(R.id.etSearchAttendees)
        rvAdminRegistrations = findViewById(R.id.rvAdminRegistrations)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // RecyclerView
        adapter = AdminRegistrationAdapter(filteredList)

        rvAdminRegistrations.layoutManager =
            LinearLayoutManager(this)

        rvAdminRegistrations.adapter = adapter

        // Search
        etSearchAttendees.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Nothing needed
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    filterRegistrations(s?.toString() ?: "")
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                    // Nothing needed
                }
            }
        )

        // Load data
        loadEventName()
        loadRegistrations()
    }

    // ============================================================
    // LOAD EVENT NAME
    // ============================================================

    private fun loadEventName() {

        if (eventId.isEmpty()) {

            Toast.makeText(
                this,
                getString(R.string.event_could_not_be_found),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        db.collection("events")
            .document(eventId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val title =
                        document.getString("title")

                    tvEventName.text =
                        title ?: getString(R.string.event_title)

                } else {

                    Toast.makeText(
                        this,
                        getString(R.string.event_could_not_be_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    getString(R.string.failed_load_events),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ============================================================
    // LOAD REGISTRATIONS
    // ============================================================

    private fun loadRegistrations() {

        if (eventId.isEmpty()) {
            return
        }

        db.collection("eventRegistrations")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { documents ->

                registrationList.clear()
                filteredList.clear()

                val totalDocuments = documents.size()

                // No registrations
                if (totalDocuments == 0) {

                    adapter.notifyDataSetChanged()

                    return@addOnSuccessListener
                }

                var completedRequests = 0

                for (document in documents) {

                    val userId =
                        document.getString("userId") ?: ""

                    // Invalid registration
                    if (userId.isEmpty()) {

                        completedRequests++

                        if (completedRequests == totalDocuments) {
                            updateList()
                        }

                        continue
                    }

                    // Load the member's user document
                    db.collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { userDocument ->

                            if (userDocument.exists()) {

                                val firstName =
                                    userDocument.getString("firstName")
                                        ?: ""

                                val lastName =
                                    userDocument.getString("lastName")
                                        ?: ""

                                val email =
                                    userDocument.getString("email")
                                        ?: ""

                                val fullName =
                                    "$firstName $lastName".trim()

                                val memberName =
                                    if (fullName.isNotEmpty()) {
                                        fullName
                                    } else {
                                        getString(
                                            R.string.unknown_member
                                        )
                                    }

                                registrationList.add(
                                    RegistrationMember(
                                        name = memberName,
                                        email = email
                                    )
                                )
                            }

                            completedRequests++

                            if (completedRequests == totalDocuments) {
                                updateList()
                            }
                        }
                        .addOnFailureListener {

                            completedRequests++

                            if (completedRequests == totalDocuments) {
                                updateList()
                            }
                        }
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    getString(R.string.failed_load_registrations),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ============================================================
    // UPDATE LIST
    // ============================================================

    private fun updateList() {

        filteredList.clear()

        filteredList.addAll(registrationList)

        adapter.notifyDataSetChanged()
    }

    // ============================================================
    // SEARCH
    // ============================================================

    private fun filterRegistrations(query: String) {

        val searchText =
            query.trim().lowercase()

        filteredList.clear()

        if (searchText.isEmpty()) {

            filteredList.addAll(registrationList)

        } else {

            for (member in registrationList) {

                val nameMatches =
                    member.name
                        .lowercase()
                        .contains(searchText)

                val emailMatches =
                    member.email
                        .lowercase()
                        .contains(searchText)

                if (nameMatches || emailMatches) {
                    filteredList.add(member)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }
}