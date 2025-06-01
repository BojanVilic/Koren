package com.koren.answers

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface AnswersUiState : UiState {
    data object Loading : AnswersUiState

    data class Shown(
        override val eventSink: (AnswersUiEvent) -> Unit
    ) : AnswersUiState, EventHandler<AnswersUiEvent>
}

sealed interface AnswersUiEvent : UiEvent {

}

sealed interface AnswersUiSideEffect : UiSideEffect {

}