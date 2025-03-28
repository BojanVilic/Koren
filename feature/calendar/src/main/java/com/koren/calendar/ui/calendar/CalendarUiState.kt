package com.koren.calendar.ui.calendar

import com.koren.common.models.calendar.Day
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState
import java.time.LocalDate

sealed interface CalendarUiState : UiState {
    data object Loading : CalendarUiState
    data class Shown(
        val groupedTasks: Map<LocalDate, List<Task>> = emptyMap(),
        val groupedEvents: Map<LocalDate, List<Event>> = emptyMap(),
        override val eventSink: (CalendarUiEvent) -> Unit
    ) : CalendarUiState, EventHandler<CalendarUiEvent>
}

sealed interface CalendarUiEvent : UiEvent {
    data class DayClicked(val day: Day) : CalendarUiEvent
}

sealed interface CalendarUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : CalendarUiSideEffect
    data class OpenDayDetails(val day: Day) : CalendarUiSideEffect
}