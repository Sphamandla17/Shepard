
package com.example.shepherd.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class RegisterResponse(
    val message: String,
    val userId: String,
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
    val createdAt: String,
    val imageUrl: String? = null
)

data class CreateAnnouncementRequest(
    val title: String,
    val message: String,
    val imageUrl: String? = null
)

data class UploadImageResponse(
    val imageUrl: String
)

interface ApiService {

    // Register a new user
    @POST("api/Users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    // Retrieve announcements
    @GET("api/Announcements")
    suspend fun getAnnouncements(): Response<List<Announcement>>

    // Publish an announcement
    @POST("api/Announcements")
    suspend fun createAnnouncement(
        @Header("Authorization") authorization: String,
        @Body request: CreateAnnouncementRequest
    ): Response<Announcement>

    // Delete an announcement (existing method preserved)
    @DELETE("api/Announcements/{id}")
    suspend fun deleteAnnouncement(
        @Path("id") id: Int
    ): Response<Unit>

    // Upload an announcement image
    @Multipart
    @POST("api/Upload/image")
    suspend fun uploadImage(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ): Response<UploadImageResponse>
}
