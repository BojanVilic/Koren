package com.koren.data.repository

import com.koren.common.models.Invitation
import com.koren.common.models.InvitationResult
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun createInvitation(): Result<InvitationResult>
    suspend fun acceptInvitation(invitationCode: String)
    suspend fun declineInvitation(invitationCode: String)
    fun getPendingInvitations(): Flow<List<Invitation>>
}