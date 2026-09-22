package com.example.shepherd

data class PrayerRequest(
    val id: String = "",
    val userId: String = "",
    val category: String = "",
    val requestText: String = "",
    val status: String = "Received",
    val pastorFeedback: String = "",
    val locationShared: Boolean = false
)