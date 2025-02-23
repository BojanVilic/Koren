package com.koren.calendar.ui.day_details

import com.koren.calendar.ui.Day
import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DayDetailsViewModel @Inject constructor(
): StateViewModel<DayDetailsUiEvent, DayDetailsUiState, DayDetailsUiSideEffect>() {

    override fun setInitialState(): DayDetailsUiState = DayDetailsUiState.Loading

    fun init(day: Day) {
        _uiState.update {
            if (day.tasks.isEmpty() && day.events.isEmpty()) {
                DayDetailsUiState.Shown.Empty(
                    day = day,
                    eventSink = { event -> handleEmptyEvent(event) }
                )
            } else {
                DayDetailsUiState.Shown.Idle(
                    day = day,
                    eventSink = { event -> handleEvent(event) }
                )
            }
        }
    }

    override fun handleEvent(event: DayDetailsUiEvent) {
        withEventfulState<DayDetailsUiState.Shown.Idle> { currentState ->
            when (event) {
                else -> Unit
            }
        }
    }

    private fun handleEmptyEvent(event: DayDetailsUiEvent) {
        withEventfulState<DayDetailsUiState.Shown.Empty> { currentState ->
            when (event) {
                is DayDetailsUiEvent.AddClicked -> {
                    _uiState.update {
                        DayDetailsUiState.Shown.AddEntry(
                            day = event.day,
                            eventSink = { event -> handleEvent(event) }
                        )
                    }
                }
            }
        }
    }

    fun handleAddEntryEvent(event: DayDetailsUiEvent) {
        withEventfulState<DayDetailsUiState.Shown.AddEntry> { currentState ->
            when (event) {
                else -> Unit
            }
        }
    }
}