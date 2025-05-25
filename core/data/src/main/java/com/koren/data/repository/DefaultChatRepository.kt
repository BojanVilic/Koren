package com.koren.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.services.AudioFileManager
import com.koren.common.services.UserSession
import com.koren.common.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

class DefaultChatRepository @Inject constructor(
    private val userSession: UserSession,
    private val database: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
    private val audioFileManager: AudioFileManager
): ChatRepository {

    companion object {
        private const val PAGE_SIZE = 15
    }

    override fun getChatMessages(): Flow<List<ChatItem>> =
        listenPage(null).map { it.first }

    override fun getOlderMessages(oldestLoadedNeg: Long): Flow<Pair<List<ChatItem>, Boolean>> =
        listenPage(oldestLoadedNeg + 1)

    private fun listenPage(startAtNeg: Long?): Flow<Pair<List<ChatItem>, Boolean>> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId
        var query: Query = database.getReference("chats/$familyId")
            .orderByChild("timestamp")
            .limitToFirst(PAGE_SIZE)
        startAtNeg?.let { query = query.startAt(it.toDouble()) }

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val msgs = snapshot.children
                    .mapNotNull { it.getValue<ChatMessage>() }
                    .map { it.copy(timestamp = -it.timestamp) }

                val page = msgs.toChatItems()
                val hasMore = msgs.size == PAGE_SIZE
                trySend(page to hasMore).isSuccess
            }

            override fun onCancelled(error: DatabaseError) = Timber.e(error.message)
        })

        awaitClose { query.removeEventListener(listener) }
    }

    private fun List<ChatMessage>.toChatItems(): List<ChatItem> = buildList {
        this@toChatItems.forEachIndexed { idx, msg ->
            add(ChatItem.MessageItem(msg))
            val next = this@toChatItems.getOrNull(idx + 1)
            if (next == null || !sameDay(msg, next)) {
                add(ChatItem.DateSeparator(msg.timestamp))
            }
        }
    }

    private fun sameDay(a: ChatMessage, b: ChatMessage): Boolean =
        Calendar.getInstance().run {
            timeInMillis = a.timestamp
            val dayA = get(Calendar.DAY_OF_YEAR); val yearA = get(Calendar.YEAR)
            timeInMillis = b.timestamp
            val dayB = get(Calendar.DAY_OF_YEAR); val yearB = get(Calendar.YEAR)
            dayA == dayB && yearA == yearB
        }

    override suspend fun sendTextMessage(messageText: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            timestamp = DateUtils.getNegativeTimeMillis(),
            messageType = MessageType.TEXT,
            textContent = messageText
        )

        val chatRef = database.getReference("chats/${user.familyId}/${message.id}")

        return try {
            withContext(Dispatchers.Default) {
                chatRef.setValue(message).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error sending message: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val messageRef = database.getReference("chats/${user.familyId}/$messageId")
        val attachmentsRef = firebaseStorage.getReference("chats/${user.familyId}/$messageId")

        return try {
            attachmentsRef.listAll().await().items.forEach { item ->
                item.delete().await()
            }
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

    override suspend fun sendImageMessage(images: Set<Uri>, messageText: String): Result<Unit> {
        val user = userSession.currentUser.first()
        val messageId = UUID.randomUUID().toString()
        val message = ChatMessage(
            id = messageId,
            senderId = user.id,
            timestamp = DateUtils.getNegativeTimeMillis(),
            messageType = MessageType.IMAGE,
            textContent = messageText,
            mediaUrls = images.map { imageUri -> uploadChatMessageImage(user.familyId, imageUri, messageId) }
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

    override suspend fun sendVideoMessage(videoUri: Uri, thumbnail: Bitmap?, duration: Long): Result<Unit> {
        val user = userSession.currentUser.first()
        val messageId = UUID.randomUUID().toString()

        val videoUrl = withContext(Dispatchers.IO) {
            uploadChatMessageVideo(user.familyId, videoUri, messageId)
        }

        val thumbnailUrl = withContext(Dispatchers.IO) {
            thumbnail?.let {
                uploadChatMessageThumbnail(user.familyId, thumbnail, messageId)
            }
        }

        val message = ChatMessage(
            id = messageId,
            senderId = user.id,
            timestamp = DateUtils.getNegativeTimeMillis(),
            messageType = MessageType.VIDEO,
            mediaUrls = listOf(videoUrl),
            mediaDuration = duration,
            thumbnailUrl = thumbnailUrl
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

    override suspend fun sendAudioMessage(audioFile: File, duration: Int): Result<Unit> {
        val user = userSession.currentUser.first()
        val messageId = UUID.randomUUID().toString()

        val audioUrl = withContext(Dispatchers.IO) {
            uploadVoiceMessage(user.familyId, audioFile, messageId)
        }

        val message = ChatMessage(
            id = messageId,
            senderId = user.id,
            timestamp = DateUtils.getNegativeTimeMillis(),
            messageType = MessageType.VOICE,
            mediaUrls = listOf(audioUrl),
            mediaDuration = duration.toLong()
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

    // In DefaultChatRepository.kt
    override suspend fun downloadAudioMessage(url: String): Result<File> {
        return try {
            val localFile = audioFileManager.getCacheFile(url)

            if (!localFile.exists() || localFile.length() == 0L) {
                val httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
                val downloadTask = httpsReference.getFile(localFile)
                downloadTask.await()

                if (!localFile.exists() || localFile.length() == 0L) {
                    Timber.e("Downloaded file is empty or missing: $localFile")
                    return Result.failure(IOException("Downloaded file is empty or missing"))
                }
            }

            Result.success(localFile)
        } catch (e: Exception) {
            Timber.e(e, "Error downloading audio: $url")
            Result.failure(e)
        }
    }

    override suspend fun getMessageById(messageId: String): Result<ChatMessage> {
        val familyId = userSession.currentUser.first().familyId
        val messageRef = database.getReference("chats/$familyId/$messageId")

        return try {
            val snapshot = messageRef.get().await()
            val message = snapshot.getValue<ChatMessage>()
                ?: return Result.failure(IllegalArgumentException("Message not found"))

            Result.success(message)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching message by ID: $messageId")
            Result.failure(e)
        }
    }

    private suspend fun uploadChatMessageImage(
        familyId: String,
        pictureUri: Uri,
        messageId: String
    ): String {
        val imageName = pictureUri.lastPathSegment ?: UUID.randomUUID().toString()
        val storageRef = firebaseStorage.getReference("chats/$familyId/$messageId/$imageName.jpg")
        storageRef.putFile(pictureUri).await()
        return storageRef.downloadUrl.await().toString()
    }

    private suspend fun uploadChatMessageVideo(
        familyId: String,
        videoUri: Uri,
        messageId: String
    ): String {
        val videoName = videoUri.lastPathSegment ?: UUID.randomUUID().toString()
        val storageRef = firebaseStorage.getReference("chats/$familyId/$messageId/$videoName.mp4")

        val uploadTask = storageRef.putFile(videoUri)

        withContext(Dispatchers.IO) {
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                Timber.d("Upload is $progress% done")
            }.addOnFailureListener { exception ->
                Timber.e(exception, "Upload failed")
            }.await()
        }

        return storageRef.downloadUrl.await().toString()
    }

    private suspend fun uploadChatMessageThumbnail(
        familyId: String,
        thumbnailBitmap: Bitmap,
        messageId: String
    ): String {
        val thumbnailName = "$messageId-thumbnail.jpg"
        val storageRef = firebaseStorage.getReference("chats/$familyId/$messageId/$thumbnailName")

        val byteArrayOutputStream = ByteArrayOutputStream()
        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val thumbnailData = byteArrayOutputStream.toByteArray()

        withContext(Dispatchers.IO) {
            storageRef.putBytes(thumbnailData).await()
        }

        return storageRef.downloadUrl.await().toString()
    }

    private suspend fun uploadVoiceMessage(
        familyId: String,
        audioFile: File,
        messageId: String
    ): String {
        val fileName = "${messageId}_voice.mp3"
        val storageRef = firebaseStorage.getReference("chats/$familyId/$messageId/$fileName")

        val uploadTask = storageRef.putFile(audioFile.toUri())

        withContext(Dispatchers.IO) {
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                Timber.d("Voice upload is $progress% done")
            }.addOnFailureListener { exception ->
                Timber.e(exception, "Voice upload failed")
            }.await()
        }

        return storageRef.downloadUrl.await().toString()
    }
}