package com.koren.calendar.ui.calendar

import androidx.lifecycle.viewModelScope
import com.koren.calendar.ui.Day
import com.koren.calendar.ui.day_details.DayDetailsScreen
import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                is CalendarUiEvent.DayClicked -> initDayDetails(event.day, currentState)
                is CalendarUiEvent.DismissBottomSheet -> _uiState.update { currentState.copy(dayDetailsContent = null) }
            }
        }
    }

    private fun initDayDetails(day: Day, currentState: CalendarUiState.Shown) {
        _uiState.update {
            CalendarUiState.Shown(
                dayDetailsContent = {
                    DayDetailsScreen(
                        day = day,
                        onDismiss = {
                            _sideEffects.emitSuspended(CalendarUiSideEffect.Dismiss)
                            viewModelScope.launch {
                                delay(300)
                                _uiState.update { currentState.copy(dayDetailsContent = null) }
                            }
                        }
                    )
                },
                eventSink = { event -> handleEvent(event) }
            )
        }
    }
}