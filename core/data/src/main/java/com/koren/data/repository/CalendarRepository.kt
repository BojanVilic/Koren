package com.koren.data.repository

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
}