package com.koren.calendar.ui.calendar

import com.koren.common.models.calendar.Day
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState
import java.time.LocalDate

sealed interface CalendarBottomSheetContent {
    data object None : CalendarBottomSheetContent
    data class DayDetails(val day: Day) : CalendarBottomSheetContent
}

sealed interface CalendarUiState : UiState {
    data object Loading : CalendarUiState
    data class Shown(
        val calendarBottomSheetContent: CalendarBottomSheetContent = CalendarBottomSheetContent.None,
        val groupedTasks: Map<LocalDate, List<Task>> = emptyMap(),
        val groupedEvents: Map<LocalDate, List<Event>> = emptyMap(),
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