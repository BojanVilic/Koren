package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.family.CallHomeRequestStatus
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateCallHomeStatusUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(status: CallHomeRequestStatus): Result<String> {
        return try {
            val user = userSession.currentUser.first()
            firebaseDatabase.reference.child("families/${user.familyId}/callHomeRequests/${user.id}/status")
                .setValue(status)
                .await()

            val message = when (status) {
                CallHomeRequestStatus.ACCEPTED -> "Call home request accepted. The requester will be notified."
                CallHomeRequestStatus.REJECTED -> "Call home request rejected. The requester will be notified."
                else -> "Call home request status updated. The requester will be notified."
            }
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update call home request."))
        }
    }
}