package com.koren.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationResult
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
        val expirationDate = creationDate + 2.days.inWholeMilliseconds
        val userData = userSession.currentUser.first()
        val invitationLink = "koren://join/$invitationId/${userData.familyId}/$invitationCode"

        val familyName = database.child("families/${userData.familyId}/name").get().await().getValue<String>()

        val invitation = Invitation(
            id = invitationId,
            familyId = userData.familyId,
            senderId = userData.id,
            invitationLink = invitationLink,
            invitationCode = invitationCode,
            status = InvitationStatus.PENDING,
            expirationDate = expirationDate,
            createdAt = creationDate,
            familyName = familyName?: "",
            senderName = userData.displayName
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

    override suspend fun acceptInvitation(invitation: Invitation, typedCode: String): Result<Unit> {
        try {
            if (invitation.invitationCode != typedCode) return Result.failure(Exception("Invalid invitation code"))

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
            return Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Failed to accept invitation: ${e.message}")
            return Result.failure(e)
        }
    }

    override suspend fun declineInvitation(id: String) {
        database.child("invitations/$id/status").setValue(InvitationStatus.DECLINED).await()
    }

    override fun getReceivedInvitations(): Flow<List<Invitation>> = callbackFlow {
        val email = userSession.currentUser.first().email
        val query = database
            .child("invitations")
            .orderByChild("recipientEmail")
            .equalTo(email)

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val invitations = snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue<Invitation>()
                }
                trySend(invitations).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun getSentInvitations(): Flow<List<Invitation>> = callbackFlow {
        val id = userSession.currentUser.first().id
        val query = database
            .child("invitations")
            .orderByChild("senderId")
            .equalTo(id)

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val invitations = snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue<Invitation>()
                }
                trySend(invitations).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

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
        val familyName = database.child("families/${userData.familyId}/name").get().await().getValue<String>()

        val invitation = Invitation(
            id = invitationId,
            familyId = userData.familyId,
            senderId = userData.id,
            invitationCode = invitationCode,
            status = InvitationStatus.PENDING,
            expirationDate = expirationDate,
            createdAt = creationDate,
            recipientEmail = email,
            familyName = familyName?: "",
            senderName = userData.displayName
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