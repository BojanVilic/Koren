package com.koren.data.repository

import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationResult
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun createInvitation(): Result<InvitationResult>
    suspend fun acceptInvitation(invitation: Invitation, typedCode: String): Result<Unit>
    suspend fun declineInvitation(id: String)
    fun getReceivedInvitations(): Flow<List<Invitation>>
    fun getSentInvitations(): Flow<List<Invitation>>
    suspend fun createInvitationViaEmail(email: String): Result<InvitationResult>
}