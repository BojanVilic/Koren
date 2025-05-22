package com.koren.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.koren.common.models.chat.ChatItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ChatRepository {
    fun getChatMessages(): Flow<List<ChatItem>>
    fun getOlderMessages(oldestLoadedNeg: Long): Flow<Pair<List<ChatItem>, Boolean>>
    suspend fun sendTextMessage(messageText: String): Result<Unit>
    suspend fun sendImageMessage(images: Set<Uri>, messageText: String): Result<Unit>
    suspend fun sendVideoMessage(videoUri: Uri, thumbnail: Bitmap?, duration: Long): Result<Unit>
    suspend fun sendAudioMessage(audioFile: File, duration: Int): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun addReactionToMessage(messageId: String, reaction: String): Result<Unit>
    suspend fun removeReactionFromMessage(messageId: String, reaction: String): Result<Unit>
}