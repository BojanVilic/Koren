package com.koren.calendar.ui.day_details

import androidx.lifecycle.viewModelScope
import com.koren.common.models.calendar.Day
import com.koren.common.util.StateViewModel
import com.koren.data.repository.CalendarRepository
import com.koren.domain.ChangeTaskStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayDetailsViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase
): StateViewModel<DayDetailsUiEvent, DayDetailsUiState, DayDetailsUiSideEffect>() {

    override fun setInitialState(): DayDetailsUiState = DayDetailsUiState.Loading

    fun init(day: Day) {
        viewModelScope.launch {
            day.localDate?.let { date ->
                combine(
                    calendarRepository.getTasksForDay(date),
                    calendarRepository.getEventsForDay(date)
                ) { tasks, events ->
                    if (tasks.isEmpty() && events.isEmpty()) {
                        DayDetailsUiState.Shown.Empty(
                            day = day,
                            eventSink = ::handleEmptyEvent
                        )
                    } else {
                        DayDetailsUiState.Shown.Idle(
                            day = day,
                            tasks = tasks,
                            events = events,
                            eventSink = ::handleEvent
                        )
                    }
                }
                .collect { state ->
                    _uiState.update { state }
                }
            }
        }
    }

    override fun handleEvent(event: DayDetailsUiEvent) {
        withEventfulState<DayDetailsUiState.Shown.Idle> { currentState ->
            when (event) {
                is DayDetailsUiEvent.AddClicked -> addEntryClicked(currentState.day)
                is DayDetailsUiEvent.TaskCompletionButtonClicked -> changeTaskStatus(event.taskId, event.completed)
            }
        }
    }

    private fun handleEmptyEvent(event: DayDetailsUiEvent) {
        withEventfulState<DayDetailsUiState.Shown.Empty> { currentState ->
            when (event) {
                is DayDetailsUiEvent.AddClicked -> addEntryClicked(currentState.day)
                else -> Unit
            }
        }
    }

    private fun changeTaskStatus(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            changeTaskStatusUseCase(taskId, isCompleted)
        }
    }

    private fun addEntryClicked(day: Day) {
        _sideEffects.emitSuspended(DayDetailsUiSideEffect.OpenAddEntry(day))
    }
}