package com.koren.calendar.ui.calendar

import androidx.compose.runtime.Composable
import com.koren.calendar.ui.Day
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface CalendarUiState : UiState {
    data object Loading : CalendarUiState
    data class Shown(
        val dayDetailsContent: @Composable (() -> Unit)? = null,
        override val eventSink: (CalendarUiEvent) -> Unit
    ) : CalendarUiState, EventHandler<CalendarUiEvent>
}

sealed interface CalendarUiEvent : UiEvent {
    data class DayClicked(val day: Day) : CalendarUiEvent
    data object DismissBottomSheet : CalendarUiEvent
}

sealed interface CalendarUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : CalendarUiSideEffect
    data object Dismiss : CalendarUiSideEffect
}