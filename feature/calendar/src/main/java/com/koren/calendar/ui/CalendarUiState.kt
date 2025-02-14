package com.koren.calendar.ui

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface CalendarUiState : UiState {
    data object Loading : CalendarUiState
    data class Shown(
        override val eventSink: (CalendarUiEvent) -> Unit
    ) : CalendarUiState, EventHandler<CalendarUiEvent>
}

sealed interface CalendarUiEvent : UiEvent {
}

sealed interface CalendarUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : CalendarUiSideEffect
}