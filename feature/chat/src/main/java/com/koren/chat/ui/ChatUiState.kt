package com.koren.chat.ui

import com.koren.common.models.chat.ChatMessage
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ChatUiState : UiState {
    data object Loading : ChatUiState
    data class Shown(
        val messages: List<ChatMessage> = emptyList(),
        val messageText: String = "",
        val showReactionPopup: Boolean = false,
        val targetMessageIdForReaction: String? = null,
        override val eventSink: (ChatUiEvent) -> Unit
    ) : ChatUiState, EventHandler<ChatUiEvent>
}

sealed interface ChatUiEvent : UiEvent {
    data class SendMessage(val message: String) : ChatUiEvent
    data class OnMessageTextChanged(val text: String) : ChatUiEvent
    data class OpenMessageReactions(val messageId: String) : ChatUiEvent
    data class OnReactionSelected(val messageId: String, val reaction: String) : ChatUiEvent
    data object DismissReactionPopup : ChatUiEvent
}

sealed interface ChatUiSideEffect : UiSideEffect {
}