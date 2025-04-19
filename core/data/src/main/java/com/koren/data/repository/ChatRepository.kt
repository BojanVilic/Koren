package com.koren.data.repository

import com.koren.common.models.chat.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    /**
     * Sends a chat message to the specified chat (family chat).
     * @param familyId The ID of the family chat.
     * @param message The message object to send.
     * @return Result indicating success or failure.
     */
    suspend fun sendMessage(familyId: String, message: ChatMessage): Result<Unit>

    /**
     * Gets a flow of chat messages for the specified chat (family chat).
     * Messages are typically ordered by timestamp.
     * @param familyId The ID of the family chat.
     * @return A Flow emitting lists of ChatMessage.
     */
    fun getMessagesFlow(familyId: String): Flow<List<ChatMessage>>
} 