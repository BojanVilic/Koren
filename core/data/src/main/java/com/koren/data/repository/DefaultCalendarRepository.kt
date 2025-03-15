package com.koren.data.repository

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class DefaultCalendarRepository @Inject constructor(
    private val userSession: UserSession
) : CalendarRepository {

    private val database = Firebase.database.reference

    override suspend fun saveEvent(
        title: String,
        description: String,
        isAllDay: Boolean,
        startDate: Long,
        endDate: Long,
        startTime: String,
        endTime: String
    ): Result<Unit> {
        return try {
            val user = userSession.currentUser.first()

            val startMillis = if (isAllDay) startDate else parseDateTime(startDate, startTime)
            val endMillis = if (isAllDay) endDate else parseDateTime(endDate, endTime)
            val newEvent = Event(
                eventId = UUID.randomUUID().toString(),
                title = title,
                description = description,
                eventStartTime = startMillis,
                eventEndTime = endMillis,
                isAllDay = isAllDay,
                creatorUserId = user.id
            )
            val familyId = userSession.currentUser.first().familyId
            database.child("families/$familyId/events/${newEvent.eventId}")
                .setValue(newEvent)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveTask(
        title: String,
        description: String,
        taskDate: Long,
        taskTime: String,
        assigneeUserId: String
    ): Result<Unit> {
        return try {
            val user = userSession.currentUser.first()
            val familyId = user.familyId
            val newTask = Task(
                taskId = UUID.randomUUID().toString(),
                title = title,
                description = description,
                taskTimestamp = parseDateTime(taskDate, taskTime),
                isCompleted = false,
                creatorUserId = user.id,
                assigneeUserId = assigneeUserId
            )
            database.child("families/$familyId/tasks/${newTask.taskId}")
                .setValue(newTask)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseDateTime(dateInMillis: Long, time: String): Long {
        if (time.isBlank()) {
            return dateInMillis
        }
        val localDate = Instant.ofEpochMilli(dateInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val localTime = LocalTime.parse(time)
        val localDateTime = LocalDateTime.of(localDate, localTime)
        return localDateTime.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}