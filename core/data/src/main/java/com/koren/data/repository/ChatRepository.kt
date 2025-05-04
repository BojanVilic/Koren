package com.koren.data.repository

import com.koren.common.models.chat.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatMessages(): Flow<List<ChatMessage>>
    suspend fun sendTextMessage(messageText: String): Result<Unit>
    suspend fun deleteMessage(messageId: String)
    suspend fun addReactionToMessage(messageId: String, reaction: String)
    suspend fun removeReactionFromMessage(messageId: String, reaction: String)
}