package com.koren.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.services.UserSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

class DefaultChatRepository @Inject constructor(
    private val userSession: UserSession,
    private val database: FirebaseDatabase
): ChatRepository {

    override fun getChatMessages(): Flow<List<ChatItem>> = callbackFlow {
        val user = userSession.currentUser.first()

        val chatRef = database.getReference("chats/${user.familyId}")
            .orderByChild("timestamp")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children
                    .mapNotNull { it.getValue<ChatMessage>() }
                    .reversed()

                val chatItems = if (messages.isEmpty()) {
                    emptyList()
                } else {
                    val items = mutableListOf<ChatItem>()

                    for (i in messages.indices) {
                        val currentMessage = messages[i]

                        items.add(ChatItem.MessageItem(currentMessage))

                        if (i < messages.size - 1) {
                            val nextMessage = messages[i + 1]

                            val currentDay = Calendar.getInstance().apply {
                                timeInMillis = currentMessage.timestamp
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            val nextDay = Calendar.getInstance().apply {
                                timeInMillis = nextMessage.timestamp
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            if (currentDay.get(Calendar.YEAR) != nextDay.get(Calendar.YEAR) ||
                                currentDay.get(Calendar.DAY_OF_YEAR) != nextDay.get(Calendar.DAY_OF_YEAR)) {
                                items.add(ChatItem.DateSeparator(currentMessage.timestamp))
                            }
                        } else {
                            items.add(ChatItem.DateSeparator(currentMessage.timestamp))
                        }
                    }
                    items
                }

                trySend(chatItems).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error fetching chat messages: ${error.message}")
            }
        }

        chatRef.addValueEventListener(listener)
        awaitClose { chatRef.removeEventListener(listener) }
    }

    override suspend fun sendTextMessage(messageText: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            timestamp = System.currentTimeMillis(),
            messageType = MessageType.TEXT,
            textContent = messageText
        )

        val chatRef = database.getReference("chats/${user.familyId}/${message.id}")

        return try {
            chatRef.setValue(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error sending message: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val messageRef = database.getReference("chats/${user.familyId}/$messageId")

        return try {
            messageRef.removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error deleting message: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun addReactionToMessage(messageId: String, reaction: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val reactionRef = database.getReference("chats/${user.familyId}/$messageId/reactions/${user.id}")

        return try {
            reactionRef.setValue(reaction).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error adding reaction: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun removeReactionFromMessage(messageId: String, reaction: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val reactionRef = database.getReference("chats/${user.familyId}/$messageId/reactions/${user.id}")

        return try {
            reactionRef.removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error removing reaction: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendImageMessage(imageUrl: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            timestamp = System.currentTimeMillis(),
            messageType = MessageType.IMAGE,
            mediaUrl = imageUrl
        )

        val chatRef = database.getReference("chats/${user.familyId}/${message.id}")

        return try {
            chatRef.setValue(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error sending image message: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendVideoMessage(videoUrl: String, duration: Long): Result<Unit> {
        val user = userSession.currentUser.first()
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            timestamp = System.currentTimeMillis(),
            messageType = MessageType.VIDEO,
            mediaUrl = videoUrl,
            mediaDuration = duration
        )

        val chatRef = database.getReference("chats/${user.familyId}/${message.id}")

        return try {
            chatRef.setValue(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error sending video message: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendAudioMessage(audioUrl: String, duration: Long): Result<Unit> {
        val user = userSession.currentUser.first()
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            timestamp = System.currentTimeMillis(),
            messageType = MessageType.VOICE,
            mediaUrl = audioUrl,
            mediaDuration = duration
        )

        val chatRef = database.getReference("chats/${user.familyId}/${message.id}")

        return try {
            chatRef.setValue(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error sending audio message: ${e.message}")
            Result.failure(e)
        }
    }
}