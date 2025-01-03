package com.koren.data.repository

import com.koren.common.models.Invitation
import com.koren.common.models.InvitationResult
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun createInvitation(): Result<InvitationResult>
    suspend fun acceptInvitation(invitation: Invitation, typedCode: String): Result<Unit>
    suspend fun declineInvitation(id: String)
    fun getAllInvitations(): Flow<List<Invitation>>
    suspend fun createInvitationViaEmail(email: String): Result<InvitationResult>
}