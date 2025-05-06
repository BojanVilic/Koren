package com.koren.common.models.chat

sealed interface ChatItem {
    val id: String

    data class MessageItem(
        val message: ChatMessage,
        override val id: String = message.id
    ) : ChatItem

    data class DateSeparator(
        val timestamp: Long,
        override val id: String = "date_$timestamp"
    ) : ChatItem
}