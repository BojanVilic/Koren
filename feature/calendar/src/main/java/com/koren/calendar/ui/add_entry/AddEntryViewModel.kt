package com.koren.calendar.ui.add_entry

import com.koren.calendar.ui.Day
import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
): StateViewModel<AddEntryUiEvent, AddEntryUiState, AddEntryUiSideEffect>() {

    override fun setInitialState(): AddEntryUiState = AddEntryUiState.Loading

    fun init(day: Day) {
        _uiState.update {
            AddEntryUiState.Shown.AddEvent(
                selectedDay = day,
                startDate = day.localDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?: 0L,
                endDate = day.localDate?.atTime(23, 59)?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?: 0L,
                eventSink = { event -> handleEvent(event) }
            )
        }
    }

    override fun handleEvent(event: AddEntryUiEvent) {
        withEventfulState<AddEntryUiState.Shown.AddEvent> { currentState ->
            when (event) {
                is AddEntryUiEvent.TitleChanged -> _uiState.update { currentState.copy(title = event.title) }
                is AddEntryUiEvent.TabChanged -> handleTabChangedEvent(event, currentState)
                is AddEntryUiEvent.CancelClicked -> handleCancelClickedEvent()
                is AddEntryUiEvent.SaveClicked -> handleSaveClickedEvent()
                is AddEntryUiEvent.DescriptionChanged -> _uiState.update { currentState.copy(description = event.description) }
                is AddEntryUiEvent.IsAllDayChanged -> _uiState.update { currentState.copy(isAllDay = event.isAllDay) }
                is AddEntryUiEvent.StartDateChanged -> _uiState.update { currentState.copy(startDate = event.startDate) }
                is AddEntryUiEvent.EndDateChanged -> _uiState.update { currentState.copy(endDate = event.endDate) }
                is AddEntryUiEvent.StartTimeChanged -> _uiState.update { currentState.copy(startTime = event.startTime) }
                is AddEntryUiEvent.EndTimeChanged -> _uiState.update { currentState.copy(endTime = event.endTime) }
            }
        }
    }

    private fun handleAddTaskEvent(event: AddEntryUiEvent) {
        withEventfulState<AddEntryUiState.Shown.AddTask> { currentState ->
            when (event) {
                is AddEntryUiEvent.TitleChanged -> _uiState.update { currentState.copy(title = event.title) }
                is AddEntryUiEvent.TabChanged -> handleTabChangedEvent(event, currentState)
                is AddEntryUiEvent.CancelClicked -> handleCancelClickedEvent()
                is AddEntryUiEvent.SaveClicked -> handleSaveClickedEvent()
                is AddEntryUiEvent.StartTimeChanged -> _uiState.update { currentState.copy(time = event.startTime) }
                else -> Unit
            }
        }
    }

    private fun handleTabChangedEvent(
        event: AddEntryUiEvent.TabChanged,
        currentState: AddEntryUiState.Shown
    ) {
        _uiState.update {
            when (event.tabIndex) {
                0 -> AddEntryUiState.Shown.AddEvent(
                    title = currentState.title,
                    selectedDay = currentState.selectedDay,
                    startDate = currentState.selectedDay.localDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?: 0L,
                    endDate = currentState.selectedDay.localDate?.atTime(23, 59)?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?: 0L,
                    eventSink = { event -> handleEvent(event) }
                )
                1 -> AddEntryUiState.Shown.AddTask(
                    title = currentState.title,
                    selectedDay = currentState.selectedDay,
                    eventSink = { event -> handleAddTaskEvent(event) }
                )
                else -> it
            }
        }
    }

    private fun handleCancelClickedEvent() {
        _sideEffects.emitSuspended(AddEntryUiSideEffect.Dismiss)
    }

    private fun handleSaveClickedEvent() {

    }
}