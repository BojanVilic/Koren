package com.koren.chat.ui

import androidx.compose.ui.text.input.TextFieldValue
import com.koren.common.models.chat.ChatMessage
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ChatUiState : UiState {
    data object Loading : ChatUiState
    data class Shown(
        val currentUserId: String = "",
        val messages: List<ChatMessage> = emptyList(),
        val messageText: TextFieldValue = TextFieldValue(""),
        val showReactionPopup: Boolean = false,
        val targetMessageIdForReaction: String? = null,
        val shownTimestamps: Set<String> = emptySet(),
        override val eventSink: (ChatUiEvent) -> Unit
    ) : ChatUiState, EventHandler<ChatUiEvent>
}

sealed interface ChatUiEvent : UiEvent {
    data object SendMessage : ChatUiEvent
    data class OnMessageTextChanged(val text: TextFieldValue) : ChatUiEvent
    data class OpenMessageReactions(val messageId: String) : ChatUiEvent
    data class OnReactionSelected(val messageId: String, val reaction: String) : ChatUiEvent
    data object DismissReactionPopup : ChatUiEvent
    data class OnMessageClicked(val messageId: String) : ChatUiEvent
}

sealed interface ChatUiSideEffect : UiSideEffect {
    data class ShowError(val message: String) : ChatUiSideEffect
}