package com.koren.calendar.ui.day_details

import androidx.lifecycle.viewModelScope
import com.koren.common.models.calendar.Day
import com.koren.common.util.StateViewModel
import com.koren.data.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayDetailsViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
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

        viewModelScope.launch {
            day.localDate?.let { date ->
                combine(
                    calendarRepository.getTasksForDay(date),
                    calendarRepository.getEventsForDay(date)
                ) { tasks, events ->
                    (_uiState.value as? DayDetailsUiState.Shown.Idle)?.copy(
                        tasks = tasks,
                        events = events
                    )
                }
                .filterIsInstance<DayDetailsUiState.Shown.Idle>()
                .collect { state ->
                    _uiState.update { state }
                }
            }
        }
    }

    override fun handleEvent(event: DayDetailsUiEvent) {
        withEventfulState<DayDetailsUiState.Shown.Idle> { currentState ->
            when (event) {
                is DayDetailsUiEvent.AddClicked -> addEntryClicked(currentState)
            }
        }
    }

    private fun handleEmptyEvent(event: DayDetailsUiEvent) {
        withEventfulState<DayDetailsUiState.Shown.Empty> { currentState ->
            when (event) {
                is DayDetailsUiEvent.AddClicked -> addEntryClicked(currentState)
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

    private fun addEntryClicked(currentState: DayDetailsUiState.Shown) {
        _uiState.update {
            DayDetailsUiState.Shown.AddEntry(
                day = currentState.day,
                eventSink = { event -> handleEvent(event) }
            )
        }
    }
}