package com.koren.data.repository

import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationResult
import com.koren.common.models.InvitationStatus
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLEncoder
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class DefaultInvitationRepository @Inject constructor(
    private val userSession: UserSession
) : InvitationRepository {

    private val database = Firebase.database.reference

    override suspend fun createInvitation(): Result<InvitationResult> {
        val invitationId = UUID.randomUUID().toString()
        val invitationCode = UUID.randomUUID().toString().substring(0, 6).uppercase()
        val creationDate = System.currentTimeMillis()
        val expirationDate = creationDate + 1.days.inWholeMilliseconds
        val userData = userSession.currentUser.first()
        val invitationLink = withContext(Dispatchers.IO) {
            "koren://join?${URLEncoder.encode("familyId=${userData.familyId}&invCode=$invitationCode", "UTF-8")}"
        }

        val invitation = Invitation(
            id = invitationId,
            familyId = userData.familyId,
            senderId = userData.id,
            invitationLink = invitationLink,
            invitationCode = invitationCode,
            status = InvitationStatus.PENDING,
            expirationDate = expirationDate,
            createdAt = creationDate
        )

        try {
            database.child("invitations/$invitationId")
                .setValue(invitation)
                .await()

            return Result.success(
                InvitationResult(
                    invitationId = invitationId,
                    familyId = userData.familyId,
                    senderName = userData.id,
                    invitationCode = invitationCode,
                    invitationLink = invitationLink
                )
            )
        } catch (e: Exception) {
            Timber.e("Failed to create invitation: ${e.message}")
            return Result.failure(e)
        }
    }

    override suspend fun acceptInvitation(invitation: Invitation) {
        val members = database.child("families/${invitation.familyId}")
            .child("members")
            .get()
            .await()
            .getValue<List<String>>()

        val userId = userSession.currentUser.first().id

        database.child("families/${invitation.familyId}/members")
            .setValue(members?.plus(userId))
            .await()
        database.child("users/$userId").child("familyId").setValue(invitation.familyId).await()

        database.child("invitations/${invitation.id}/status").setValue(InvitationStatus.ACCEPTED).await()
    }

    override suspend fun declineInvitation(invitationCode: String) {
        TODO("Not yet implemented")
    }

    override fun getPendingInvitations(): Flow<List<Invitation>> = flow {
        val email = userSession.currentUser.first().email
        val pendingInvitations = database
            .child("invitations")
            .orderByChild("recipientEmail")
            .equalTo(email)
            .values<List<Invitation>>()
            .mapNotNull { invitations ->
                invitations?.filter { it.status == InvitationStatus.PENDING }
            }
            .flowOn(Dispatchers.IO)

        emitAll(pendingInvitations)
    }

    override suspend fun createInvitationViaEmail(email: String): Result<InvitationResult> {
        val query = database
            .child("users")
            .orderByChild("email")
            .equalTo(email)
            .get()
            .await()

        val userData = userSession.currentUser.first()

        if (query.hasChildren().not()) return Result.failure(Exception("User with email $email does not exist."))
        if (userData.email == email) return Result.failure(Exception("You cannot invite yourself, you silly goose!"))

        val invitationId = UUID.randomUUID().toString()
        val invitationCode = UUID.randomUUID().toString().substring(0, 6).uppercase()
        val creationDate = System.currentTimeMillis()
        val expirationDate = creationDate + 1.days.inWholeMilliseconds

        val invitation = Invitation(
            id = invitationId,
            familyId = userData.familyId,
            senderId = userData.id,
            invitationCode = invitationCode,
            status = InvitationStatus.PENDING,
            expirationDate = expirationDate,
            createdAt = creationDate,
            recipientEmail = email
        )

        try {
            database.child("invitations/$invitationId")
                .setValue(invitation)
                .await()

            return Result.success(
                InvitationResult(
                    invitationId = invitationId,
                    familyId = userData.familyId,
                    senderName = userData.id,
                    invitationCode = invitationCode,
                )
            )
        } catch (e: Exception) {
            Timber.e("Failed to create invitation: ${e.message}")
            return Result.failure(e)
        }
    }
}