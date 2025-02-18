package com.koren.calendar.ui.add_entry

import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
): StateViewModel<AddEntryUiEvent, AddEntryUiState, AddEntryUiSideEffect>() {

    override fun setInitialState(): AddEntryUiState = AddEntryUiState.Loading

    init {
        _uiState.update {
            AddEntryUiState.Shown.AddEvent(
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
                    eventSink = { event -> handleEvent(event) }
                )
                1 -> AddEntryUiState.Shown.AddTask(
                    title = currentState.title,
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