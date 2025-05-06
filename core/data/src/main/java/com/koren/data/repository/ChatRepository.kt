package com.koren.data.repository

import com.koren.common.models.chat.ChatItem
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatMessages(): Flow<List<ChatItem>>
    suspend fun sendTextMessage(messageText: String): Result<Unit>
    suspend fun sendImageMessage(imageUrl: String): Result<Unit>
    suspend fun sendVideoMessage(videoUrl: String, duration: Long): Result<Unit>
    suspend fun sendAudioMessage(audioUrl: String, duration: Long): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun addReactionToMessage(messageId: String, reaction: String): Result<Unit>
    suspend fun removeReactionFromMessage(messageId: String, reaction: String): Result<Unit>
}