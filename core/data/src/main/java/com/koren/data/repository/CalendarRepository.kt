package com.koren.data.repository

import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.EventWithUsers
import com.koren.common.models.calendar.Task
import com.koren.common.models.calendar.TaskWithUsers
import com.koren.common.util.HourMinute
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarRepository {
    suspend fun saveEvent(
        title: String,
        description: String,
        isAllDay: Boolean,
        startDate: Long,
        endDate: Long,
        startTime: HourMinute?,
        endTime: HourMinute?
    ): Result<Unit>

    suspend fun saveTask(
        title: String = "",
        description: String = "",
        taskDate: Long,
        taskTime: HourMinute?,
        assigneeUserId: String = ""
    ): Result<Unit>

    fun getEvents(): Flow<List<Event>>
    fun getTasks(): Flow<List<Task>>

    fun getEventsForDay(date: LocalDate): Flow<List<Event>>
    fun getTasksForDay(date: LocalDate): Flow<List<Task>>
    fun getFirstUpcomingTask(): Flow<TaskWithUsers?>
    fun getFirstUpcomingEvent(): Flow<EventWithUsers?>
}