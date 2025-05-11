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
    val mediaUrls: List<String>? = null,
    val mediaDuration: Long? = null,
    val thumbnailUrl: String? = null,
    val reactions: Map<String, String>? = null
)