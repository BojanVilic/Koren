package com.koren.chat.ui

import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ChatUiState : UiState {
    data object Loading : ChatUiState
    data class Error(val message: String) : ChatUiState
    data object NoFamily : ChatUiState
    data class Success(
        val messages: List<ChatMessage>,
        val currentUser: UserData = UserData(),
        val messageInput: String = "",
        val isSending: Boolean = false,
        override val eventSink: (ChatEvent) -> Unit
    ) : ChatUiState, EventHandler<ChatEvent>
}

sealed interface ChatEvent : UiEvent {
    data class MessageInputChanged(val text: String) : ChatEvent
    data object SendMessageClicked : ChatEvent
}

sealed interface ChatSideEffect : UiSideEffect {
    data class ShowErrorSnackbar(val message: String) : ChatSideEffect
}