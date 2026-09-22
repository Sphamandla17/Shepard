package com.example.shepherd

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminMembersActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved language
        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_admin_members)

        firestore = FirebaseFirestore.getInstance()

        val recyclerMembers =
            findViewById<RecyclerView>(R.id.recyclerMembers)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        recyclerMembers.layoutManager =
            LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        loadMembers(recyclerMembers)
    }

    private fun loadMembers(
        recyclerMembers: RecyclerView
    ) {

        firestore
            .collection("users")
            .get()
            .addOnSuccessListener { documents ->

                val members = mutableListOf<Member>()

                for (document in documents) {

                    val member = Member(
                        firstName =
                            document.getString("firstName")
                                ?: "",

                        lastName =
                            document.getString("lastName")
                                ?: "",

                        email =
                            document.getString("email")
                                ?: "",

                        phone =
                            document.getString("phone")
                                ?: "",

                        role =
                            document.getString("role")
                                ?: "member"
                    )

                    members.add(member)
                }

                recyclerMembers.adapter =
                    AdminMemberAdapter(members)
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    getString(R.string.members_load_failed) +
                            (exception.message ?: ""),
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}