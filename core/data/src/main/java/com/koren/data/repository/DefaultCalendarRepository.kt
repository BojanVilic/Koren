package com.koren.data.repository

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.EventWithUsers
import com.koren.common.models.calendar.Task
import com.koren.common.models.calendar.TaskWithUsers
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.DateUtils.convertToUtcWithLocalOffset
import com.koren.common.util.DateUtils.toEpochMilliDayEnd
import com.koren.common.util.DateUtils.toEpochMilliDayStart
import com.koren.common.util.HourMinute
import com.koren.common.util.toLocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
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
        startTime: HourMinute?,
        endTime: HourMinute?
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
                allDay = isAllDay,
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
        taskTime: HourMinute?,
        assigneeUserId: String
    ): Result<Unit> {
        if (taskTime == null) return Result.failure(Exception("Task time is required"))
        return try {
            val user = userSession.currentUser.first()
            val familyId = user.familyId
            val newTask = Task(
                taskId = UUID.randomUUID().toString(),
                title = title,
                description = description,
                taskTimestamp = parseDateTime(taskDate, taskTime),
                completed = false,
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

    override fun getEvents(): Flow<List<Event>> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId
        val ref = database.child("families/$familyId/events")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children
                    .mapNotNull { it.getValue<Event>() }
                trySend(events).isSuccess
            }
            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun getTasks(): Flow<List<Task>> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId
        val ref = database.child("families/$familyId/tasks")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children
                    .mapNotNull { it.getValue<Task>() }
                trySend(tasks).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun getEventsForDay(date: LocalDate): Flow<List<Event>> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId
        val startOfDay = date.toEpochMilliDayStart()
        val endOfDay = date.toEpochMilliDayEnd()

        val ref = database.child("families/$familyId/events")
            .orderByChild("eventEndTime")
            .startAt(startOfDay.toDouble())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allEvents = snapshot.children.mapNotNull { it.getValue<Event>() }
                val eventsForDay = allEvents.filter {
                    it.eventStartTime <= endOfDay
                }
                trySend(eventsForDay).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun getTasksForDay(date: LocalDate): Flow<List<Task>> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId

        val startOfDay = date.toEpochMilliDayStart()
        val endOfDay = date.toEpochMilliDayEnd()

        val ref = database.child("families/$familyId/tasks")
            .orderByChild("taskTimestamp")
            .startAt(startOfDay.toDouble())
            .endAt(endOfDay.toDouble())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children.mapNotNull { it.getValue<Task>() }
                trySend(tasks).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)


    override fun getTasksForDayAndUser(date: LocalDate): Flow<List<Task>> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId
        val userId = userSession.currentUser.first().id

        val startOfDay = date.toEpochMilliDayStart()
        val endOfDay = date.toEpochMilliDayEnd()

        val ref = database.child("families/$familyId/tasks")
            .orderByChild("taskTimestamp")
            .startAt(startOfDay.toDouble())
            .endAt(endOfDay.toDouble())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children
                    .mapNotNull { it.getValue<Task>() }
                    .filter { it.assigneeUserId == userId }
                trySend(tasks).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun getFirstUpcomingTask(): Flow<TaskWithUsers?> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId
        val userId = userSession.currentUser.first().id
        val ref = database.child("families/$familyId/tasks")
            .orderByChild("taskTimestamp")
            .startAt(System.currentTimeMillis().toDouble())
            .limitToFirst(1)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val task = snapshot.children
                        .mapNotNull { it?.getValue<Task>() }
                        .firstOrNull { it.assigneeUserId == userId }

                    if (task != null) {
                        val creator = withContext(Dispatchers.IO) {
                            val userRef = database.child("users/${task.creatorUserId}")
                            val userSnapshot = userRef.get().await()
                            userSnapshot.getValue<UserData>()
                        }
                        val assignee = withContext(Dispatchers.IO) {
                            val userRef = database.child("users/${task.assigneeUserId}")
                            val userSnapshot = userRef.get().await()
                            userSnapshot.getValue<UserData>()
                        }
                        val taskWithUsers = TaskWithUsers(
                            taskId = task.taskId,
                            title = task.title,
                            description = task.description,
                            taskTimestamp = task.taskTimestamp,
                            completed = task.completed,
                            creator = creator,
                            assignee = assignee
                        )
                        trySend(taskWithUsers).isSuccess
                    } else {
                        trySend(null).isSuccess
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getFirstUpcomingEvent(): Flow<EventWithUsers?> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId
        val ref = database.child("families/$familyId/events")
            .orderByChild("eventStartTime")
            .startAt(System.currentTimeMillis().toDouble())
            .limitToFirst(1)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val event = snapshot.children.firstOrNull()?.getValue<Event>()
                    if (event != null) {
                        val creator = withContext(Dispatchers.IO) {
                            val userRef = database.child("users/${event.creatorUserId}")
                            val userSnapshot = userRef.get().await()
                            userSnapshot.getValue<UserData>()
                        }
                        val eventWithUsers = EventWithUsers(
                            eventId = event.eventId,
                            title = event.title,
                            description = event.description,
                            eventStartTime = event.eventStartTime,
                            eventEndTime = event.eventEndTime,
                            allDay = event.allDay,
                            creator = creator
                        )
                        trySend(eventWithUsers).isSuccess
                    } else {
                        trySend(null).isSuccess
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private fun parseDateTime(dateInMillis: Long, time: HourMinute?): Long {
        if (time == null) return dateInMillis
        val localDate = Instant.ofEpochMilli(dateInMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        val localTime = time.toLocalTime()
        val localDateTime = LocalDateTime.of(localDate, localTime)
        return localDateTime.atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
            .convertToUtcWithLocalOffset()
    }
}