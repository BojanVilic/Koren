package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.database.getValue

@Singleton
class GetAttachmentForMessageUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(messageId: String): Result<List<String>> {
        val familyId = userSession.currentUser.first().familyId
        val messageRef = firebaseDatabase.getReference("chats/$familyId/$messageId/mediaUrls")
        return try {
            val mediaUrls = messageRef
                .get()
                .await()
                .children
                .sortedBy { it.key?.toIntOrNull() }
                .mapNotNull { it.getValue<String>() }

            Result.success(mediaUrls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}