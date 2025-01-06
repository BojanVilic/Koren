package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationStatus
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AcceptQRInvitationUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(invitation: Invitation, qrInvCode: String): Result<Unit> {
        try {
            if (invitation.invitationCode != qrInvCode) return Result.failure(Exception("Invalid invitation code"))

            val members = firebaseDatabase.reference.child("families/${invitation.familyId}")
                .child("members")
                .get()
                .await()
                .getValue<List<String>>()

            val userId = userSession.currentUser.first().id

            firebaseDatabase.reference.child("families/${invitation.familyId}/members")
                .setValue(members?.plus(userId))
                .await()
            firebaseDatabase.reference.child("users/$userId").child("familyId").setValue(invitation.familyId).await()
            firebaseDatabase.reference.child("invitations/${invitation.id}/status").setValue(InvitationStatus.ACCEPTED).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Failed to accept invitation: ${e.message}")
            return Result.failure(e)
        }
    }
}