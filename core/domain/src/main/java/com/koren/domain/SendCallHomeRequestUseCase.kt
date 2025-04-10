package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.family.CallHomeRequest
import com.koren.common.models.family.CallHomeRequestStatus
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SendCallHomeRequestUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {

    suspend operator fun invoke(targetUserId: String): Result<Unit> {
        val user = userSession.currentUser.first()

        val requestPath = firebaseDatabase.reference
            .child("families/${user.familyId}/callHomeRequests/$targetUserId")

        try {
            val existingRequestSnapshot = requestPath.get().await()

            if (existingRequestSnapshot.exists()) return Result.failure(ExistingRequestException())

            val callHomeRequest = CallHomeRequest(
                targetUserId = targetUserId,
                requesterId = user.id,
                timestamp = System.currentTimeMillis(),
                status = CallHomeRequestStatus.REQUESTED
            )

            requestPath.setValue(callHomeRequest).await()

            return Result.success(Unit)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}

class ExistingRequestException(message: String = "A call home request for this user is already pending.") : Exception(message)