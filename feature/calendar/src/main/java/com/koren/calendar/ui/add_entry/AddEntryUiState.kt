package com.koren.calendar.ui.add_entry

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface AddEntryUiState : UiState {
    data object Loading : AddEntryUiState
    sealed interface Shown : AddEntryUiState, EventHandler<AddEntryUiEvent> {
        val title: String

        data class AddEvent(
            override val title: String = "",
            val description: String = "",
            val isAllDay: Boolean = true,
            val startDate: Long = 0L,
            val endDate: Long = 0L,
            val startTime: String = "",
            val endTime: String = "",
            override val eventSink: (AddEntryUiEvent) -> Unit
        ) : Shown

        data class AddTask(
            override val title: String = "",
            override val eventSink: (AddEntryUiEvent) -> Unit
        ) : Shown
    }
}

sealed interface AddEntryUiEvent : UiEvent {
    data class TitleChanged(val title: String) : AddEntryUiEvent
    data class TabChanged(val tabIndex: Int) : AddEntryUiEvent
    data class DescriptionChanged(val description: String) : AddEntryUiEvent
    data class IsAllDayChanged(val isAllDay: Boolean) : AddEntryUiEvent
    data class StartDateChanged(val startDate: Long) : AddEntryUiEvent
    data class EndDateChanged(val endDate: Long) : AddEntryUiEvent
    data class StartTimeChanged(val startTime: String) : AddEntryUiEvent
    data class EndTimeChanged(val endTime: String) : AddEntryUiEvent
    data object SaveClicked : AddEntryUiEvent
    data object CancelClicked : AddEntryUiEvent
}

sealed interface AddEntryUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : AddEntryUiSideEffect
    data object Dismiss : AddEntryUiSideEffect
}