package com.koren.chat.ui

import androidx.compose.ui.text.input.TextFieldValue
import com.koren.common.models.chat.ChatItem
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ChatUiState : UiState {
    data object Loading : ChatUiState
    data class Shown(
        val currentUserId: String = "",
        val chatItems: List<ChatItem> = emptyList(),
        val messageText: TextFieldValue = TextFieldValue(""),
        val showReactionPopup: Boolean = false,
        val targetMessageIdForReaction: String? = null,
        val shownTimestamps: Set<String> = emptySet(),
        val attachmentsOverlayShown: Boolean = true,
        val profilePicsMap: Map<String, String> = emptyMap(),
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
    data object ShowAttachmentsOverlay : ChatUiEvent
    data object CloseAttachmentsOverlay : ChatUiEvent
}

sealed interface ChatUiSideEffect : UiSideEffect {
    data class ShowError(val message: String) : ChatUiSideEffect
}