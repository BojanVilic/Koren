package com.koren.calendar.ui.day_details

import com.koren.common.models.calendar.Day
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface DayDetailsUiState : UiState {
    data object Loading : DayDetailsUiState

    sealed interface Shown : DayDetailsUiState, EventHandler<DayDetailsUiEvent> {
        val day: Day

        data class Empty(
            override val day: Day,
            override val eventSink: (DayDetailsUiEvent) -> Unit
        ) : Shown

        data class Idle(
            override val day: Day,
            val tasks: List<Task> = emptyList(),
            val events: List<Event> = emptyList(),
            override val eventSink: (DayDetailsUiEvent) -> Unit
        ) : Shown
    }
}

sealed interface DayDetailsUiEvent : UiEvent {
    data object AddClicked : DayDetailsUiEvent
}

sealed interface DayDetailsUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : DayDetailsUiSideEffect
    data class OpenAddEntry(val day: Day) : DayDetailsUiSideEffect
}