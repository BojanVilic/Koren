package com.koren.calendar.ui.calendar

import androidx.lifecycle.viewModelScope
import com.koren.calendar.ui.Day
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
                is CalendarUiEvent.DayClicked -> initDayDetails(event.day)
                is CalendarUiEvent.ResetCalendarBottomSheetContent -> dismissBottomSheet(currentState)
            }
        }
    }

    private fun dismissBottomSheet(currentState: CalendarUiState.Shown) {
        viewModelScope.launch {
            delay(200)
            _uiState.update { currentState.copy(calendarBottomSheetContent = CalendarBottomSheetContent.None) }
        }
    }

    private fun initDayDetails(day: Day) {
        _uiState.update {
            CalendarUiState.Shown(
                calendarBottomSheetContent = CalendarBottomSheetContent.DayDetails(day),
                eventSink = { event -> handleEvent(event) }
            )
        }
    }
}