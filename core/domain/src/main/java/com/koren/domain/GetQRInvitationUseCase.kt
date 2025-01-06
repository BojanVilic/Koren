package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetQRInvitationUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend operator fun invoke(
        invId: String,
        familyId: String,
        invCode: String
    ): Result<Invitation> {

        val invitation = firebaseDatabase.reference.database.getReference("invitations/$invId")
            .get()
            .await()
            .getValue<Invitation>()?: return Result.failure(Exception("Invitation not found."))

        if (invitation.expirationDate < System.currentTimeMillis()) return Result.failure(Exception("Invitation has expired."))
        if (invitation.status != InvitationStatus.PENDING) return Result.failure(Exception("Invitation is no longer valid."))
        if (invitation.familyId != familyId) return Result.failure(Exception("Invitation not found."))
        if (invitation.invitationCode != invCode) return Result.failure(Exception("There was an error with the invitation code."))

        return Result.success(invitation)
    }
}