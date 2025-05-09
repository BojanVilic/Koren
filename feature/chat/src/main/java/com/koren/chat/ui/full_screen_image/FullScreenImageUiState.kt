package com.koren.chat.ui.full_screen_image

import com.koren.common.util.*

sealed interface FullScreenImageUiState : UiState {
    data object Loading : FullScreenImageUiState
    data class Shown(
        val mediaUrl: String,
        override val eventSink: (FullScreenImageUiEvent) -> Unit
    ) : FullScreenImageUiState, EventHandler<FullScreenImageUiEvent>
}

sealed interface FullScreenImageUiEvent : UiEvent {
}

sealed interface FullScreenImageUiSideEffect : UiSideEffect {

}