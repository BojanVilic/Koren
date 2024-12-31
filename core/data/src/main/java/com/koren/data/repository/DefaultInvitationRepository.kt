package com.koren.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationResult
import com.koren.common.models.InvitationStatus
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
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

    override suspend fun acceptInvitation(invitationCode: String) {
        TODO("Not yet implemented")
    }

    override suspend fun declineInvitation(invitationCode: String) {
        TODO("Not yet implemented")
    }

    override fun getPendingInvitations(): Flow<List<Invitation>> = callbackFlow {
        val userId = userSession.currentUser.first().id
        val query = database
            .child("invitations")
            .orderByChild("senderId")
            .equalTo(userId)

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
}