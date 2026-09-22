package com.example.shepherd

import com.google.firebase.Timestamp

data class NotificationItem(
    val title: String = "",
    val message: String = "",
    val createdAt: Timestamp? = null
)