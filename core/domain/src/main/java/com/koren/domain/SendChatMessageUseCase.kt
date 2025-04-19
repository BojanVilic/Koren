package com.koren.domain

import com.koren.common.models.chat.ChatMessage
import com.koren.common.services.UserSession
import com.koren.data.repository.ChatRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userSession: UserSession
) {
    /**
     * Invokes the use case to send a chat message.
     * It populates sender information from the current user session.
     * @param text The message text to send.
     * @return Result indicating success or failure.
     */
    suspend operator fun invoke(text: String): Result<Unit> {
        if (text.isBlank()) return Result.failure(IllegalArgumentException("Message text cannot be blank."))

        return try {
            val currentUser = userSession.currentUser.first()
            val familyId = currentUser.familyId

            if (familyId.isBlank()) {
                return Result.failure(IllegalStateException("User does not belong to a family."))
            }

            val message = ChatMessage(
                senderId = currentUser.id,
                senderName = currentUser.displayName,
                senderProfilePictureUrl = currentUser.profilePictureUrl,
                text = text
            )

            chatRepository.sendMessage(familyId, message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}