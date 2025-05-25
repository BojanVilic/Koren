package com.koren.chat.ui.chat.more_options

import com.koren.common.models.chat.ChatMessage
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface MoreOptionsUiState : UiState {
    data object Loading : MoreOptionsUiState
    data class Shown(
        val message: ChatMessage = ChatMessage(),
        override val eventSink: (MoreOptionsUiEvent) -> Unit
    ) : MoreOptionsUiState, EventHandler<MoreOptionsUiEvent>
}

sealed interface MoreOptionsUiEvent : UiEvent {
    data object DeleteMessage : MoreOptionsUiEvent
}

sealed interface MoreOptionsUiSideEffect : UiSideEffect {
    data object NavigateBack : MoreOptionsUiSideEffect
}