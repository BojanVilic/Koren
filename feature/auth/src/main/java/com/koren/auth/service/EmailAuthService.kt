package com.koren.auth.service

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmailAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    companion object {
        fun FirebaseException.parsePasswordRequirements(): List<String> {
            val regex = Regex("PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: \\[(.*?)\\]")
            val match = regex.find(this.message ?: "")
            return if (match != null) {
                match.groupValues[1].split(", ").map { it.trim() }
            } else {
                emptyList()
            }
        }
    }

    fun signUp(email: String, password: String, result: (Result<Unit>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) Result.success(Unit)
            else Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}