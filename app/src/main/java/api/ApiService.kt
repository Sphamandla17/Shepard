package com.example.shepherd.api

import retrofit2.Response
import retrofit2.http.Body
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

interface ApiService {

    @POST("api/Users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}