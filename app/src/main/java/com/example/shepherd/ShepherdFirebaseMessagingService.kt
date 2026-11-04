package com.example.shepherd

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ShepherdFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        fun updateTokenForCurrentUser() {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val uid = currentUser.uid
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (!token.isNullOrEmpty()) {
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("users").document(uid)
                            .update("fcmTokens", FieldValue.arrayUnion(token))
                            .addOnFailureListener {
                                firestore.collection("users").document(uid)
                                    .set(mapOf("fcmTokens" to listOf(token)), com.google.firebase.firestore.SetOptions.merge())
                            }
                    }
                }
            }
        }

        fun removeTokenOnSignOut() {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val uid = currentUser.uid
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (!token.isNullOrEmpty()) {
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .update("fcmTokens", FieldValue.arrayRemove(token))
                            .addOnFailureListener {
                                // Ignore failure on logout
                            }
                    }
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateTokenForCurrentUser()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationType = remoteMessage.data["notificationType"]
        val announcementIdStr = remoteMessage.data["announcementId"]

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Shepherd"

        val message = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        showNotification(title, message, notificationType, announcementIdStr, remoteMessage.data)
    }

    private fun showNotification(
        title: String,
        message: String,
        notificationType: String?,
        announcementIdStr: String?,
        data: Map<String, String>
    ) {
        val channelId = "shepherd_notifications"

        val intent = if (notificationType == "announcement" && announcementIdStr != null) {
            Intent(this, AnnouncementDetailsActivity::class.java).apply {
                putExtra("id", announcementIdStr.toIntOrNull() ?: 0)
                putExtra("title", data["title"] ?: title)
                putExtra("message", data["body"] ?: message)
                putExtra("createdAt", data["createdAt"] ?: "")
                putExtra("createdBy", data["createdBy"] ?: "")
                putExtra("imageUrl", data["imageUrl"])
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        } else {
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Shepherd Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
