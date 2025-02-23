package com.koren.calendar.ui.calendar

import com.koren.calendar.ui.Day
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface CalendarBottomSheetContent {
    data object None : CalendarBottomSheetContent
    data class DayDetails(val day: Day) : CalendarBottomSheetContent
}

sealed interface CalendarUiState : UiState {
    data object Loading : CalendarUiState
    data class Shown(
        val calendarBottomSheetContent: CalendarBottomSheetContent = CalendarBottomSheetContent.None,
        override val eventSink: (CalendarUiEvent) -> Unit
    ) : CalendarUiState, EventHandler<CalendarUiEvent>
}

sealed interface CalendarUiEvent : UiEvent {
    data class DayClicked(val day: Day) : CalendarUiEvent
    data object ResetCalendarBottomSheetContent : CalendarUiEvent
}

sealed interface CalendarUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : CalendarUiSideEffect
}