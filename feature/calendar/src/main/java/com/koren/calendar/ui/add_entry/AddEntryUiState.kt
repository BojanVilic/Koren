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
    data object SaveClicked : AddEntryUiEvent
    data object CancelClicked : AddEntryUiEvent
}

sealed interface AddEntryUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : AddEntryUiSideEffect
    data object Dismiss : AddEntryUiSideEffect
}