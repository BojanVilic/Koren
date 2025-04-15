package com.koren.domain

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.koren.common.models.calendar.Task
import com.koren.common.models.calendar.TaskTimeRange
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetAssignedTasksForUserUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    operator fun invoke(
        userId: String,
        range: TaskTimeRange = TaskTimeRange.Next7Days
    ): Flow<List<Task>> = callbackFlow {

        val familyId = userSession.currentUser.first().familyId

        if (familyId.isBlank()) {
            Timber.w("Cannot get tasks, user or familyId is null/blank for userId: $userId")
            trySend(emptyList())
            close(IllegalStateException("User or familyId not found for task query."))
            return@callbackFlow
        }

        val now = Instant.now()
        val rangeStartMillis = now.toEpochMilli()
        val rangeEndMillis = when (range) {
            TaskTimeRange.Next24Hours -> now.plus(1, ChronoUnit.DAYS).toEpochMilli()
            TaskTimeRange.Next7Days -> now.plus(7, ChronoUnit.DAYS).toEpochMilli()
            TaskTimeRange.Next14Days -> now.plus(14, ChronoUnit.DAYS).toEpochMilli()
            TaskTimeRange.Next30Days -> now.plus(30, ChronoUnit.DAYS).toEpochMilli()
        }

        val query = firebaseDatabase.reference.child("families/$familyId/tasks")
            .orderByChild("assigneeUserId")
            .equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children
                    .mapNotNull { it.getValue(Task::class.java) }
                    .filter { task ->
                        task.taskTimestamp in rangeStartMillis..rangeEndMillis
                    }
                trySend(tasks).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Firebase task query cancelled for user $userId, range $range")
                cancel("Firebase query failed for assigned tasks.", error.toException())
            }
        }

        query.addValueEventListener(listener)

        awaitClose {
            Timber.d("Removing task listener for user $userId, range $range")
            query.removeEventListener(listener)
        }
    }.flowOn(Dispatchers.IO)
}