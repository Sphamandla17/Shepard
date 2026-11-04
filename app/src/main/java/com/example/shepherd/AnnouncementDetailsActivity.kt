package com.example.shepherd

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AnnouncementDetailsActivity : ComponentActivity() {

    private lateinit var btnDownloadImage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLanguage(
            this,
            LanguageHelper.getSavedLanguage(this)
        )

        setContentView(R.layout.activity_announcement_details)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val ivAnnouncementImage = findViewById<ImageView>(R.id.ivAnnouncementImage)
        btnDownloadImage = findViewById(R.id.btnDownloadImage)
        val tvContent = findViewById<TextView>(R.id.tvContent)

        btnBack.setOnClickListener {
            finish()
        }

        val announcementId = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: "Announcement"
        val createdAt = intent.getStringExtra("createdAt") ?: ""
        val createdBy = intent.getStringExtra("createdBy") ?: ""
        val message = intent.getStringExtra("message") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl")

        tvTitle.text = title

        val dateText = if (createdBy.isNotEmpty()) {
            "Posted by: $createdBy" + if (createdAt.isNotEmpty()) " • $createdAt" else ""
        } else {
            createdAt
        }
        tvDate.text = dateText
        tvContent.text = message

        if (!imageUrl.isNullOrEmpty()) {
            ivAnnouncementImage.visibility = View.VISIBLE
            btnDownloadImage.visibility = View.VISIBLE
            ivAnnouncementImage.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_logo_placeholder)
                error(R.drawable.ic_logo_placeholder)
            }

            btnDownloadImage.setOnClickListener {
                downloadAndSaveImage(imageUrl, announcementId)
            }
        } else {
            ivAnnouncementImage.visibility = View.GONE
            btnDownloadImage.visibility = View.GONE
        }
    }

    private fun downloadAndSaveImage(imageUrl: String, announcementId: Int) {
        btnDownloadImage.isEnabled = false
        btnDownloadImage.text = "Downloading..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                if (bitmap == null) {
                    throw Exception("Could not decode image from URL.")
                }

                val filename = "Shepherd_Announcement_${if (announcementId > 0) announcementId else System.currentTimeMillis()}.jpg"
                var saved = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/The Shepherd")
                    }
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        resolver.openOutputStream(imageUri)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                            saved = true
                        }
                    }
                } else {
                    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val shepherdDir = File(picturesDir, "The Shepherd")
                    if (!shepherdDir.exists()) {
                        shepherdDir.mkdirs()
                    }
                    val file = File(shepherdDir, filename)
                    FileOutputStream(file).use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                        saved = true
                    }
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    mediaScanIntent.data = Uri.fromFile(file)
                    sendBroadcast(mediaScanIntent)
                }

                withContext(Dispatchers.Main) {
                    btnDownloadImage.isEnabled = true
                    btnDownloadImage.text = "Download Image"
                    if (saved) {
                        Toast.makeText(this@AnnouncementDetailsActivity, "Image saved to Gallery.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@AnnouncementDetailsActivity, "Failed to save image.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnDownloadImage.isEnabled = true
                    btnDownloadImage.text = "Download Image"
                    Toast.makeText(this@AnnouncementDetailsActivity, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
