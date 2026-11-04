
package com.example.shepherd

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.shepherd.api.CreateAnnouncementRequest
import com.example.shepherd.api.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class CreateAnnouncementActivity : ComponentActivity() {

    private var selectedImageUri: Uri? = null

    private lateinit var imagePreview: ImageView
    private lateinit var uploadPlaceholder: LinearLayout
    private lateinit var btnPublish: Button

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                selectedImageUri = uri

                imagePreview.setImageURI(uri)
                imagePreview.visibility = View.VISIBLE
                uploadPlaceholder.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_create_announcement)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etTitle = findViewById<EditText>(R.id.etAnnouncementTitle)
        val etMessage = findViewById<EditText>(R.id.etAnnouncementMessage)

        btnPublish = findViewById(R.id.btnPublish)

        val uploadCard =
            findViewById<androidx.cardview.widget.CardView>(
                R.id.cardUploadImage
            )

        imagePreview = findViewById(R.id.ivAnnouncementPreview)

        uploadPlaceholder =
            findViewById(R.id.layoutUploadPlaceholder)

        btnBack.setOnClickListener {
            finish()
        }

        uploadCard.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnPublish.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val message = etMessage.text.toString().trim()

            if (title.isEmpty()) {
                etTitle.error = "Please enter an announcement title"
                etTitle.requestFocus()
                return@setOnClickListener
            }

            if (message.isEmpty()) {
                etMessage.error = "Please enter an announcement message"
                etMessage.requestFocus()
                return@setOnClickListener
            }

            btnPublish.isEnabled = false

            lifecycleScope.launch {
                try {
                    val session = AuthRepository.getCurrentSession(
                        forceRefresh = true
                    )

                    if (session == null) {
                        Toast.makeText(
                            this@CreateAnnouncementActivity,
                            "Please sign in again. Your session could not be verified.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    if (session.role !in listOf("admin", "pastor")) {
                        Toast.makeText(
                            this@CreateAnnouncementActivity,
                            "You are not authorised to publish announcements.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    var imageUrl: String? = null

                    selectedImageUri?.let { uri ->

                        Toast.makeText(
                            this@CreateAnnouncementActivity,
                            "Uploading image...",
                            Toast.LENGTH_SHORT
                        ).show()

                        val inputStream = contentResolver.openInputStream(uri)
                            ?: throw Exception(
                                "Could not read selected image."
                            )

                        val mimeType = contentResolver.getType(uri)
                            ?: "image/jpeg"

                        val extension = when (mimeType.lowercase()) {
                            "image/png" -> ".png"
                            "image/webp" -> ".webp"
                            else -> ".jpg"
                        }

                        val tempFile = File(
                            cacheDir,
                            "announcement_${System.currentTimeMillis()}$extension"
                        )

                        inputStream.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                input.copyTo(output)
                            }
                        }

                        try {
                            val requestBody = tempFile.asRequestBody(
                                mimeType.toMediaTypeOrNull()
                            )

                            val imagePart =
                                MultipartBody.Part.createFormData(
                                    "file",
                                    tempFile.name,
                                    requestBody
                                )

                            val uploadResponse =
                                RetrofitClient.apiService.uploadImage(
                                    authorization = "Bearer ${session.idToken}",
                                    file = imagePart
                                )

                            if (!uploadResponse.isSuccessful) {
                                val errorBody =
                                    uploadResponse.errorBody()?.string()
                                        ?: "No error details returned by server."

                                throw Exception(
                                    "Image upload failed (${uploadResponse.code()}): $errorBody"
                                )
                            }

                            imageUrl = uploadResponse.body()?.imageUrl
                                ?: throw Exception(
                                    "Image URL was not returned."
                                )

                        } finally {
                            tempFile.delete()
                        }
                    }

                    val request = CreateAnnouncementRequest(
                        title = title,
                        message = message,
                        imageUrl = imageUrl
                    )

                    val response =
                        RetrofitClient.apiService.createAnnouncement(
                            authorization = "Bearer ${session.idToken}",
                            request = request
                        )

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@CreateAnnouncementActivity,
                            "Announcement published successfully!",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()

                    } else {
                        val errorBody =
                            response.errorBody()?.string()
                                ?: "No error details returned by server."

                        Toast.makeText(
                            this@CreateAnnouncementActivity,
                            "Publishing failed (${response.code()}): $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@CreateAnnouncementActivity,
                        "Publishing failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                } finally {
                    btnPublish.isEnabled = true
                }
            }
        }
    }
}
