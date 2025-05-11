package com.koren.chat.ui.full_screen_video

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface FullScreenVideoUiState : UiState {
    data object Loading : FullScreenVideoUiState

    data class Shown(
        val videoUrl: String,
        override val eventSink: (FullScreenVideoUiEvent) -> Unit
    ) : FullScreenVideoUiState, EventHandler<FullScreenVideoUiEvent>
}

sealed interface FullScreenVideoUiEvent : UiEvent {

}

sealed interface FullScreenVideoUiSideEffect : UiSideEffect {

}