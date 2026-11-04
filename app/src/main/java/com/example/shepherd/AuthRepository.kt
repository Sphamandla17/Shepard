package com.example.shepherd

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class AuthSessionInfo(
    val uid: String,
    val idToken: String,
    val role: String
)

object AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    /**
     * Retrieves the currently authenticated Firebase user, obtains their Firebase ID token,
     * and retrieves their role from their existing Firestore profile at `users/{uid}`.
     * Validates that the role is strictly one of `admin`, `pastor`, or `member`.
     * Returns null if no user is signed in, if token generation fails, if the profile does not exist,
     * if the role is missing/blank/unauthorized, or if an error occurs.
     */
    suspend fun getCurrentSession(forceRefresh: Boolean = false): AuthSessionInfo? {
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid

        return suspendCancellableCoroutine { continuation ->
            var resumed = false

            fun safeResume(result: AuthSessionInfo?) {
                if (!resumed && continuation.isActive) {
                    resumed = true
                    continuation.resume(result)
                }
            }

            currentUser.getIdToken(forceRefresh)
                .addOnCompleteListener { tokenTask ->
                    if (!tokenTask.isSuccessful) {
                        safeResume(null)
                        return@addOnCompleteListener
                    }

                    val idToken = tokenTask.result?.token
                    if (idToken.isNullOrEmpty()) {
                        safeResume(null)
                        return@addOnCompleteListener
                    }

                    firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                safeResume(null)
                                return@addOnSuccessListener
                            }

                            val roleRaw = document.getString("role")?.trim()?.lowercase()
                            if (roleRaw.isNullOrEmpty() || (roleRaw != "admin" && roleRaw != "pastor" && roleRaw != "member")) {
                                safeResume(null)
                                return@addOnSuccessListener
                            }

                            safeResume(
                                AuthSessionInfo(
                                    uid = uid,
                                    idToken = idToken,
                                    role = roleRaw
                                )
                            )
                        }
                        .addOnFailureListener {
                            safeResume(null)
                        }
                }
        }
    }
}
