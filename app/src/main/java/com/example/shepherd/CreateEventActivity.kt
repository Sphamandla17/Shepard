package com.example.shepherd

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class CreateEventActivity : AppCompatActivity() {

    private lateinit var etEventTitle: EditText
    private lateinit var etEventDescription: EditText
    private lateinit var etEventDate: EditText
    private lateinit var etEventTime: EditText
    private lateinit var etEventLocation: EditText
    private lateinit var etMaxCapacity: EditText
    private lateinit var btnPublish: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_create_event)

        etEventTitle = findViewById(R.id.etEventTitle)
        etEventDescription = findViewById(R.id.etEventDescription)
        etEventDate = findViewById(R.id.etEventDate)
        etEventTime = findViewById(R.id.etEventTime)
        etEventLocation = findViewById(R.id.etEventLocation)
        etMaxCapacity = findViewById(R.id.etMaxCapacity)
        btnPublish = findViewById(R.id.btnPublish)

        findViewById<ImageButton>(R.id.btnBack)
            .setOnClickListener {
                finish()
            }

        etEventDate.setOnClickListener {
            showDatePicker()
        }

        etEventTime.setOnClickListener {
            showTimePicker()
        }

        btnPublish.setOnClickListener {
            publishEvent()
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val formattedDate =
                    String.format(
                        "%02d/%02d/%04d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )

                etEventDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    private fun showTimePicker() {

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->

                val formattedTime =
                    String.format(
                        "%02d:%02d",
                        selectedHour,
                        selectedMinute
                    )

                etEventTime.setText(formattedTime)
            },
            hour,
            minute,
            true
        )

        timePicker.show()
    }

    private fun publishEvent() {

        val title =
            etEventTitle.text.toString().trim()

        val description =
            etEventDescription.text.toString().trim()

        val date =
            etEventDate.text.toString().trim()

        val time =
            etEventTime.text.toString().trim()

        val location =
            etEventLocation.text.toString().trim()

        val capacityText =
            etMaxCapacity.text.toString().trim()

        // VALIDATION

        if (title.isEmpty()) {

            etEventTitle.error =
                getString(R.string.enter_event_title)

            return
        }

        if (description.isEmpty()) {

            etEventDescription.error =
                getString(R.string.enter_event_description)

            return
        }

        if (date.isEmpty()) {

            etEventDate.error =
                getString(R.string.select_event_date)

            return
        }

        if (time.isEmpty()) {

            etEventTime.error =
                getString(R.string.select_event_time)

            return
        }

        if (location.isEmpty()) {

            etEventLocation.error =
                getString(R.string.enter_event_location)

            return
        }

        if (capacityText.isEmpty()) {

            etMaxCapacity.error =
                getString(R.string.enter_max_capacity)

            return
        }

        val maxCapacity =
            capacityText.toIntOrNull()

        if (maxCapacity == null || maxCapacity <= 0) {

            etMaxCapacity.error =
                getString(R.string.valid_capacity)

            return
        }

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                getString(R.string.please_log_in_again),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        btnPublish.isEnabled = false

        val event = hashMapOf(
            "title" to title,
            "description" to description,
            "date" to date,
            "time" to time,
            "location" to location,
            "maxCapacity" to maxCapacity,
            "registeredCount" to 0,
            "createdBy" to currentUser.uid,
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection("events")
            .add(event)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    getString(
                        R.string.event_published_successfully
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener {

                btnPublish.isEnabled = true

                Toast.makeText(
                    this,
                    getString(
                        R.string.failed_publish_event
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}