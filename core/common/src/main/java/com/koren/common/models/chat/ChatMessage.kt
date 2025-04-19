package com.koren.common.models.chat

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderProfilePictureUrl: String? = null,
    val text: String = "",
    val timestamp: Long = 0L
) 