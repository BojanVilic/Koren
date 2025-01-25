package com.koren.data.repository

import android.location.Location
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.activity.LocationActivity
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
import com.koren.domain.UpdateLastUserActivityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class DefaultActivityRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession,
    private val updateLastUserActivityUseCase: UpdateLastUserActivityUseCase,
    private val locationService: LocationService
) : ActivityRepository {

    companion object {
        const val LOCATION_ACTIVITY = "location"
    }

    override suspend fun insertNewActivity(location: Location) {
        val userData = userSession.currentUser.first()
        val lastActivityIdRef = firebaseDatabase.reference.child("users/${userData.id}/lastActivityId")
        val lastActivityId = lastActivityIdRef
            .get()
            .await()
            .getValue<String>()

        if (lastActivityId?.isEmpty() == true) {
            val activity = LocationActivity(
                id = UUID.randomUUID().toString(),
                userId = userData.id,
                userDisplayName = userData.displayName,
                familyId = userData.familyId,
                createdAt = System.currentTimeMillis(),
                locationName = locationService.getLocationName(location),
                inTransit = location.speed * 3.6 >= 5
            )
            Timber.d("No last location found")
            firebaseDatabase.getReference(activitiesPath(activity.familyId, activity.id, LOCATION_ACTIVITY)).setValue(activity)
            updateLastUserActivityUseCase(activity.id)
        } else {
            val activity = LocationActivity(
                id = UUID.randomUUID().toString(),
                userId = userData.id,
                userDisplayName = userData.displayName,
                familyId = userData.familyId,
                createdAt = System.currentTimeMillis(),
                locationName = locationService.getLocationName(location),
                inTransit = location.speed * 3.6 >= 5
            )

            firebaseDatabase.getReference(activitiesPath(userData.familyId, lastActivityId?: "", LOCATION_ACTIVITY))
                .get()
                .await()
                .getValue<LocationActivity>()
                ?.let { lastActivity ->
                    val timeDifferenceInMins = (activity.createdAt - lastActivity.createdAt).milliseconds.inWholeMinutes
                    Timber.d("Time difference: $timeDifferenceInMins")
                    if (timeDifferenceInMins >= 5 && lastActivity.locationName.equals(activity.locationName, true).not()) {
                        if (lastActivity.inTransit) {
                            firebaseDatabase.getReference(activitiesPath(lastActivity.familyId, lastActivity.id, LOCATION_ACTIVITY)).removeValue()
                        }
                        firebaseDatabase.getReference(activitiesPath(activity.familyId, activity.id, LOCATION_ACTIVITY)).setValue(activity)
                        updateLastUserActivityUseCase(activity.id)
                    }
                }
        }
    }

    override fun getLocationActivities(): Flow<List<LocationActivity>> = callbackFlow {
        val userData = userSession.currentUser.first()

        firebaseDatabase.reference.child("activities/${userData.familyId}/location")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val activities = snapshot.children
                        .mapNotNull { it.getValue<LocationActivity>() }
                        .sortedByDescending { it.createdAt }

                    trySend(activities).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.message)
                }
            })

        awaitClose()
    }.flowOn(Dispatchers.Default)

    private fun activitiesPath(familyId: String, activityId: String, type: String) = "activities/${familyId}/$type/${activityId}"
}