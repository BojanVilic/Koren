package com.koren.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.services.UserSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class DefaultChatRepository @Inject constructor(
    private val userSession: UserSession,
    private val database: FirebaseDatabase
): ChatRepository {

    override fun getChatMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val user = userSession.currentUser.first()

        val chatRef = database.getReference("chats/${user.familyId}")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children
                    .mapNotNull { it.getValue<ChatMessage>() }

                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error fetching chat messages: ${error.message}")
            }
        }

        chatRef.addValueEventListener(listener)
        awaitClose { chatRef.removeEventListener(listener) }
    }

    override suspend fun sendTextMessage(messageText: String) {
        val user = userSession.currentUser.first()
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            timestamp = System.currentTimeMillis(),
            messageType = MessageType.TEXT,
            textContent = messageText
        )

        val chatRef = database.getReference("chats/${user.familyId}/${message.id}")

        chatRef.setValue(message)
            .addOnSuccessListener {
                Timber.d("Message sent successfully")
            }
            .addOnFailureListener { error ->
                Timber.e("Error sending message: ${error.message}")
            }
    }

    override suspend fun deleteMessage(messageId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addReactionToMessage(messageId: String, reaction: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeReactionFromMessage(messageId: String, reaction: String) {
        TODO("Not yet implemented")
    }
}