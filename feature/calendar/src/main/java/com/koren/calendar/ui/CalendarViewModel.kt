package com.koren.calendar.ui

import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
): StateViewModel<CalendarUiEvent, CalendarUiState, CalendarUiSideEffect>() {

    override fun setInitialState(): CalendarUiState = CalendarUiState.Loading

    init {
        _uiState.update {
            CalendarUiState.Shown(
                eventSink = { event -> handleEvent(event) }
            )
        }
    }
    override fun handleEvent(event: CalendarUiEvent) {
        withEventfulState<CalendarUiState.Shown> { currentState ->
            when (event) {
                else -> {}
            }
        }
    }
}