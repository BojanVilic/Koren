package com.koren.common.models.chat

enum class MessageType {
    TEXT, IMAGE, VIDEO, VOICE
}

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val timestamp: Long = 0L,
    val messageType: MessageType = MessageType.TEXT,
    val textContent: String? = null,
    val mediaUrl: String? = null,
    val mediaDuration: Long? = null,
    val reactions: Map<String, String>? = null
)

data class ChatConversation(
    val chatId: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessageTimestamp: Long = 0L,
    val lastMessageText: String? = null,
    val unreadCount: Map<String, Int> = emptyMap()
)