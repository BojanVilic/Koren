package com.koren.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.chat.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class DefaultChatRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : ChatRepository {

    companion object {
        private const val CHATS_NODE = "chats"
        private const val MESSAGES_NODE = "messages"
    }

    override suspend fun sendMessage(familyId: String, message: ChatMessage): Result<Unit> {
        return try {
            if (familyId.isBlank()) {
                return Result.failure(IllegalArgumentException("Family ID cannot be blank."))
            }
            val chatRef = firebaseDatabase.getReference(CHATS_NODE).child(familyId).child(MESSAGES_NODE)
            val messageId = chatRef.push().key ?: throw IllegalStateException("Couldn't get push key for chat message")
            
            val messageData = mapOf(
                "id" to messageId,
                "senderId" to message.senderId,
                "senderName" to message.senderName,
                "senderProfilePictureUrl" to message.senderProfilePictureUrl,
                "text" to message.text,
                "timestamp" to ServerValue.TIMESTAMP
            )

            chatRef.child(messageId).setValue(messageData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send chat message to family $familyId")
            Result.failure(e)
        }
    }

    override fun getMessagesFlow(familyId: String): Flow<List<ChatMessage>> = callbackFlow {
        if (familyId.isBlank()) {
            send(emptyList())
            close(IllegalArgumentException("Family ID cannot be blank."))
            return@callbackFlow
        }

        val messagesRef = firebaseDatabase.getReference(CHATS_NODE).child(familyId).child(MESSAGES_NODE)
        val query = messagesRef.orderByChild("timestamp")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { dataSnapshot ->
                    try {
                        dataSnapshot.getValue<ChatMessage>()
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to parse chat message: ${dataSnapshot.key}")
                        null
                    }
                }
                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Firebase message listener cancelled for family $familyId")
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)

        awaitClose { query.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)
} 