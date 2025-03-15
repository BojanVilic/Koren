package com.koren.calendar.ui.add_entry

import androidx.lifecycle.viewModelScope
import com.koren.calendar.ui.Day
import com.koren.common.models.calendar.Event
import com.koren.common.util.StateViewModel
import com.koren.data.repository.CalendarRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase,
    private val calendarRepository: CalendarRepository
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
                is AddEntryUiEvent.SaveClicked -> {
                    _uiState.update {
                        val newState = currentState.copy(showErrors = true)
                        handleSaveClickedEvent(newState)
                        currentState.copy(showErrors = true)
                    }
                }
                is AddEntryUiEvent.DescriptionChanged -> _uiState.update { currentState.copy(description = event.description) }
                is AddEntryUiEvent.IsAllDayChanged -> _uiState.update { currentState.copy(isAllDay = event.isAllDay) }
                is AddEntryUiEvent.StartDateChanged -> _uiState.update { currentState.copy(startDate = event.startDate) }
                is AddEntryUiEvent.EndDateChanged -> _uiState.update { currentState.copy(endDate = event.endDate) }
                is AddEntryUiEvent.StartTimeChanged -> _uiState.update { currentState.copy(startTime = event.startTime) }
                is AddEntryUiEvent.EndTimeChanged -> _uiState.update { currentState.copy(endTime = event.endTime) }
                else -> Unit
            }
        }
    }

    private fun handleAddTaskEvent(event: AddEntryUiEvent) {
        withEventfulState<AddEntryUiState.Shown.AddTask> { currentState ->
            when (event) {
                is AddEntryUiEvent.TitleChanged -> _uiState.update { currentState.copy(title = event.title) }
                is AddEntryUiEvent.TabChanged -> handleTabChangedEvent(event, currentState)
                is AddEntryUiEvent.CancelClicked -> handleCancelClickedEvent()
                is AddEntryUiEvent.SaveClicked -> {
                    _uiState.update {
                        val newState = currentState.copy(showErrors = true)
                        handleSaveClickedEvent(newState)
                        currentState.copy(showErrors = true)
                    }
                }
                is AddEntryUiEvent.StartTimeChanged -> _uiState.update { currentState.copy(time = event.startTime) }
                is AddEntryUiEvent.DescriptionChanged -> _uiState.update { currentState.copy(description = event.description) }
                is AddEntryUiEvent.AssigneeSearchQueryChanged -> {
                    val filtered = currentState.allFamilyMembers.filter {
                        it.displayName.contains(event.query, ignoreCase = true)
                    }
                    _uiState.update {
                        currentState.copy(
                            assigneeSearchQuery = event.query,
                            filteredAssignees = filtered
                        )
                    }
                }
                is AddEntryUiEvent.AssigneeDropdownExpandedChanged -> _uiState.update { currentState.copy(assigneeDropdownExpanded = event.expanded) }
                is AddEntryUiEvent.AssigneeSelected -> _uiState.update { currentState.copy(
                    assigneeSearchQuery = event.assignee.displayName,
                    selectedAssignee = event.assignee
                ) }
                is AddEntryUiEvent.RemoveSelectedAssignee -> _uiState.update { currentState.copy(
                    assigneeSearchQuery = "",
                    selectedAssignee = null
                ) }
                else -> Unit
            }
        }
    }

    private fun handleTabChangedEvent(
        event: AddEntryUiEvent.TabChanged,
        currentState: AddEntryUiState.Shown
    ) {
        viewModelScope.launch {
            _uiState.update {
                when (event.tabIndex) {
                    0 -> AddEntryUiState.Shown.AddEvent(
                        title = currentState.title,
                        selectedDay = currentState.selectedDay,
                        startDate = currentState.selectedDay.localDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?: 0L,
                        endDate = currentState.selectedDay.localDate?.atTime(23, 59)?.toInstant(ZoneOffset.UTC)?.toEpochMilli()?: 0L,
                        eventSink = { event -> handleEvent(event) }
                    )
                    1 -> {
                        val allFamilyMembers = getAllFamilyMembersUseCase().first()

                        AddEntryUiState.Shown.AddTask(
                            title = currentState.title,
                            selectedDay = currentState.selectedDay,
                            eventSink = { event -> handleAddTaskEvent(event) },
                            filteredAssignees = allFamilyMembers,
                            allFamilyMembers = allFamilyMembers
                        )
                    }
                    else -> it
                }
            }
        }
    }

    private fun handleCancelClickedEvent() {
        _sideEffects.emitSuspended(AddEntryUiSideEffect.Dismiss)
    }

    private fun handleSaveClickedEvent(currentState: AddEntryUiState.Shown) {
        if (currentState.hasErrors) return
        when (currentState) {
            is AddEntryUiState.Shown.AddEvent -> {
                viewModelScope.launch {
                    calendarRepository.saveEvent(
                        title = currentState.title,
                        description = currentState.description,
                        isAllDay = currentState.isAllDay,
                        startDate = currentState.startDate,
                        endDate = currentState.endDate,
                        startTime = currentState.startTime,
                        endTime = currentState.endTime
                    )
                    _sideEffects.emitSuspended(AddEntryUiSideEffect.Dismiss)
                }
            }
            is AddEntryUiState.Shown.AddTask -> {
                viewModelScope.launch {
                    calendarRepository.saveTask(
                        title = currentState.title,
                        description = currentState.description,
                        taskDate = currentState.selectedDay.localDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0L,
                        taskTime = currentState.time,
                        assigneeUserId = currentState.selectedAssignee?.id ?: ""
                    )
                    _sideEffects.emitSuspended(AddEntryUiSideEffect.Dismiss)
                }
            }
        }
    }
}