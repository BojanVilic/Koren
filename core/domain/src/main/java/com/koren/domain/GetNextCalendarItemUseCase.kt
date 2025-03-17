package com.koren.domain

import com.koren.common.models.calendar.CalendarItem
import com.koren.data.repository.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetNextCalendarItemUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    operator fun invoke(): Flow<CalendarItem> {
        return combine(
            calendarRepository.getFirstUpcomingEvent(),
            calendarRepository.getFirstUpcomingTask()
        ) { event, task ->
            when {
                event != null && task != null ->
                    if (event.eventStartTime < task.taskTimestamp) {
                        CalendarItem.EventItem(event)
                    } else {
                        CalendarItem.TaskItem(task)
                    }
                event != null -> CalendarItem.EventItem(event)
                task != null -> CalendarItem.TaskItem(task)
                else -> CalendarItem.None
            }
        }
    }
}