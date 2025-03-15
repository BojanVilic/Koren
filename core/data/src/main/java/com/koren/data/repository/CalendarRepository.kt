package com.koren.data.repository

import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    suspend fun saveEvent(
        title: String,
        description: String,
        isAllDay: Boolean,
        startDate: Long,
        endDate: Long,
        startTime: String,
        endTime: String
    ): Result<Unit>

    suspend fun saveTask(
        title: String = "",
        description: String = "",
        taskDate: Long,
        taskTime: String,
        assigneeUserId: String = ""
    ): Result<Unit>

    suspend fun getEvents(): Flow<List<Event>>
    suspend fun getTasks(): Flow<List<Task>>
}