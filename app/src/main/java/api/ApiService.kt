package com.example.shepherd.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class RegisterResponse(
    val message: String,
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)

data class Announcement(
    val id: Int,
    val title: String,
    val message: String,
    val createdBy: String,
    val createdAt: String
)

interface ApiService {

    @POST("api/Users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("api/Announcements")
    suspend fun getAnnouncements():
            Response<List<Announcement>>
}