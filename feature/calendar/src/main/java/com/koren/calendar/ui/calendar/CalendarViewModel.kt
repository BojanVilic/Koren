package com.koren.calendar.ui.calendar

import androidx.lifecycle.viewModelScope
import com.koren.calendar.ui.Day
import com.koren.common.models.calendar.Event
import com.koren.common.util.DateUtils.toLocalDate
import com.koren.common.util.StateViewModel
import com.koren.data.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
): StateViewModel<CalendarUiEvent, CalendarUiState, CalendarUiSideEffect>() {

    override fun setInitialState(): CalendarUiState = CalendarUiState.Loading

    init {
        _uiState.update {
            CalendarUiState.Shown(
                eventSink = { event -> handleEvent(event) }
            )
        }

        viewModelScope.launch {
            combine(
                calendarRepository.getTasks(),
                calendarRepository.getEvents()
            ) { tasks, events ->
                val groupedTasks = tasks.groupBy { it.taskTimestamp.toLocalDate() }
                val groupedEvents = events.expandEventsByDay()

                (_uiState.value as? CalendarUiState.Shown)?.let {
                    it.copy(
                        groupedTasks = groupedTasks,
                        groupedEvents = groupedEvents,
                        eventSink = { event -> handleEvent(event) }
                    )
                }
            }
            .filterIsInstance<CalendarUiState.Shown>()
            .collect { state ->
                _uiState.update { state }
            }
        }
    }

    override fun handleEvent(event: CalendarUiEvent) {
        withEventfulState<CalendarUiState.Shown> { currentState ->
            when (event) {
                is CalendarUiEvent.DayClicked -> initDayDetails(currentState, event.day)
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

    private fun initDayDetails(
        currentState: CalendarUiState.Shown,
        day: Day
    ) {
        _uiState.update {
            currentState.copy(
                calendarBottomSheetContent = CalendarBottomSheetContent.DayDetails(day),
                eventSink = { event -> handleEvent(event) }
            )
        }
    }

    private fun List<Event>.expandEventsByDay(): Map<LocalDate, List<Event>> {
        return this
            .flatMap { event ->
                val start = Instant.ofEpochMilli(event.eventStartTime).atZone(ZoneOffset.UTC).toLocalDate()
                val end = Instant.ofEpochMilli(event.eventEndTime).atZone(ZoneOffset.UTC).toLocalDate()
                val daysInRange = generateSequence(start) { it.plusDays(1) }
                    .takeWhile { !it.isAfter(end) }

                daysInRange.map { date -> date to event }
            }
            .groupBy({ it.first }, { it.second })
    }
}