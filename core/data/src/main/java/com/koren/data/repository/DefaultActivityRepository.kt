package com.koren.data.repository

import android.location.Location
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.activity.LocationActivity
import com.koren.common.models.activity.UserLocationActivity
import com.koren.common.models.user.UserData
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
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
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class DefaultActivityRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession,
    private val locationService: LocationService
) : ActivityRepository {

    companion object {
        const val LOCATION_ACTIVITY = "location"
    }

    override suspend fun insertNewActivity(location: Location) {
        val userData = userSession.currentUser.first()
        if (userData.familyId.isEmpty()) return

        val lastActivityIdRef = firebaseDatabase.reference.child("users/${userData.id}/lastActivityId")
        val lastActivityId = lastActivityIdRef.get().await().getValue<String>()

        val newActivity = LocationActivity(
            id = UUID.randomUUID().toString(),
            userId = userData.id,
            familyId = userData.familyId,
            createdAt = System.currentTimeMillis(),
            locationName = locationService.getLocationName(location),
            inTransit = location.speed * 3.6 >= 5
        )

        if (lastActivityId.isNullOrEmpty()) {
            firebaseDatabase.getReference(activitiesPath(newActivity.familyId, newActivity.id, LOCATION_ACTIVITY))
                .setValue(newActivity)
            updateUserLocation(newActivity.id)
            Timber.d("No last location found, created new activity.")
        } else {
            val lastActivity = firebaseDatabase
                .getReference(activitiesPath(userData.familyId, lastActivityId, LOCATION_ACTIVITY))
                .get()
                .await()
                .getValue<LocationActivity>() ?: return

            val timeDifferenceInMins = (newActivity.createdAt - lastActivity.createdAt).milliseconds.inWholeMinutes

            // 1) Same name -> do not update
            if (lastActivity.locationName.equals(newActivity.locationName, ignoreCase = true)) return

            // 2) Less than 5 minutes -> do not update
            if (timeDifferenceInMins < 5) return

            // 3) If both in transit -> remove old transit entry
            if (lastActivity.inTransit && newActivity.inTransit) {
                firebaseDatabase.getReference("users/${lastActivity.userId}/lastActivityId").removeValue()
                firebaseDatabase.getReference(activitiesPath(lastActivity.familyId, lastActivity.id, LOCATION_ACTIVITY))
                    .removeValue()
            }

            firebaseDatabase.getReference(activitiesPath(newActivity.familyId, newActivity.id, LOCATION_ACTIVITY))
                .setValue(newActivity)
            updateUserLocation(newActivity.id)
            Timber.d("Activity updated: ${newActivity.id}")
        }
    }

    override fun getLocationActivities(): Flow<List<UserLocationActivity>> = callbackFlow {
        val userData = userSession.currentUser.first()
        val activitiesRef = firebaseDatabase.reference.child("activities/${userData.familyId}/location")
        val pageSize = 10

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val activities = snapshot.children
                        .mapNotNull { it.getValue<LocationActivity>() }
                        .sortedByDescending { it.createdAt }
                        .map { activity ->
                            val user = withContext(Dispatchers.IO) {
                                val userRef = firebaseDatabase.reference.child("users/${activity.userId}")
                                val userSnapshot = userRef.get().await()
                                userSnapshot.getValue<UserData>()
                            }
                            UserLocationActivity(
                                id = activity.id,
                                userData = user,
                                familyId = activity.familyId,
                                createdAt = activity.createdAt,
                                locationName = activity.locationName,
                                inTransit = activity.inTransit
                            )
                        }

                    trySend(activities).isSuccess
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        activitiesRef.orderByChild("createdAt").limitToLast(pageSize).addValueEventListener(listener)
        awaitClose {
            activitiesRef.removeEventListener(listener)
        }
    }.flowOn(Dispatchers.Default)

    override fun getMoreLocationActivities(
        lastCreatedAt: Long
    ): Flow<Pair<List<UserLocationActivity>, Boolean>> = callbackFlow {
        val userData = userSession.currentUser.first()
        val activitiesRef = firebaseDatabase.reference.child("activities/${userData.familyId}/location")
        val pageSize = 10

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val activities = snapshot.children
                        .mapNotNull { it.getValue<LocationActivity>() }
                        .sortedByDescending { it.createdAt }
                        .map { activity ->
                            val user = withContext(Dispatchers.IO) {
                                val userRef = firebaseDatabase.reference.child("users/${activity.userId}")
                                val userSnapshot = userRef.get().await()
                                userSnapshot.getValue<UserData>()
                            }
                            UserLocationActivity(
                                id = activity.id,
                                userData = user,
                                familyId = activity.familyId,
                                createdAt = activity.createdAt,
                                locationName = activity.locationName,
                                inTransit = activity.inTransit
                            )
                        }

                    // If we got fewer than pageSize, we know there are no more
                    val hasMore = activities.size == pageSize
                    trySend(activities to hasMore).isSuccess
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        }

        activitiesRef.orderByChild("createdAt")
            .endBefore(lastCreatedAt.toDouble())
            .limitToLast(pageSize)
            .addValueEventListener(listener)

        awaitClose {
            activitiesRef.removeEventListener(listener)
        }
    }.flowOn(Dispatchers.Default)

    private fun activitiesPath(familyId: String, activityId: String, type: String) = "activities/${familyId}/$type/${activityId}"

    private suspend fun updateUserLocation(activityId: String) {
        val user = userSession.currentUser.first()

        firebaseDatabase.reference.child("users/${user.id}/lastActivityId")
            .setValue(activityId)
            .await()
    }
}