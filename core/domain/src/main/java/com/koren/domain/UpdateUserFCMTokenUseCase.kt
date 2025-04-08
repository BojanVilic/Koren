package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateUserFCMTokenUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {

    suspend operator fun invoke(token: String): Result<Unit> {
        val user = userSession.currentUser.first()

        try {
            firebaseDatabase.reference.child("users/${user.id}/fcmToken")
                .setValue(token)
                .await()
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}