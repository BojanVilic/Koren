package com.koren.domain

import com.koren.common.models.chat.ChatMessage
import com.koren.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Invokes the use case to get a flow of messages for a given family chat.
     * @param familyId The ID of the family.
     * @return A Flow emitting lists of ChatMessage.
     */
    operator fun invoke(familyId: String): Flow<List<ChatMessage>> {
        if (familyId.isBlank()) {
            return flowOf(emptyList())
        }
        return chatRepository.getMessagesFlow(familyId)
    }
} 