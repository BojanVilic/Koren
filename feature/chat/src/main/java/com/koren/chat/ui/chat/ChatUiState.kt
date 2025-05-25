package com.koren.chat.ui.chat

import com.koren.chat.ui.chat.message_input.MessageInputUiState
import com.koren.chat.ui.chat.messages_window.MessagesWindowUiState
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ChatUiState : UiState {
    data object Loading : ChatUiState
    data class Shown(
        val messagesWindowUiState: MessagesWindowUiState = MessagesWindowUiState.Loading,
        val messageInputUiState: MessageInputUiState = MessageInputUiState()
    ) : ChatUiState
}

sealed interface ChatUiEvent : UiEvent {
}

sealed interface ChatUiSideEffect : UiSideEffect {
    data class ShowError(val message: String) : ChatUiSideEffect
    data class NavigateToImageAttachment(val messageId: String) : ChatUiSideEffect
    data class NavigateToFullScreenImage(val mediaUrl: String) : ChatUiSideEffect
    data class NavigateToFullScreenVideo(val videoUrl: String) : ChatUiSideEffect
    data class NavigateToMoreOptions(val messageId: String) : ChatUiSideEffect
}