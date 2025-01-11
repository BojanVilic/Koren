package com.koren.auth.service

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class EmailAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    companion object {
        fun FirebaseException.parsePasswordRequirements(): List<String> {
            val regex = Regex("PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: \\[(.*)\\]")
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
}