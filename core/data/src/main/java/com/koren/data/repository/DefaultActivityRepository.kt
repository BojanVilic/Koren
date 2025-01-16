package com.koren.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.activity.LocationActivity
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class DefaultActivityRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) : ActivityRepository {

    companion object {
        const val LOCATION_ACTIVITY = "location"
    }

    override suspend fun insertNewActivity(activity: LocationActivity) {
        val userData = userSession.currentUser.first()

        firebaseDatabase.reference.child("activities/${activity.familyId}/location")
            .orderByChild("userId")
            .equalTo(userData.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastLocation = snapshot.children.mapNotNull {
                        it.getValue<LocationActivity>()
                    }.maxByOrNull {
                        it.createdAt
                    }

                    if (lastLocation != null) {
                        val timeDifferenceInMins = (activity.createdAt - lastLocation.createdAt).milliseconds.inWholeMinutes
                        if (timeDifferenceInMins > 5 && lastLocation.locationName.equals(activity.locationName, true).not()) {
                            firebaseDatabase.getReference(activitiesPath(activity, LOCATION_ACTIVITY))
                                .setValue(activity)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException())
                }
            })
    }

    override fun getActivities(): Flow<List<LocationActivity>> = callbackFlow {
        val userData = userSession.currentUser.first()

        firebaseDatabase.reference.child("activities/${userData.familyId}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val locationResponse = snapshot.child(LOCATION_ACTIVITY).children.map {
                        it.getValue<LocationActivity>()
                    }.filterNotNull()

                    trySend(locationResponse).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose()
    }.flowOn(Dispatchers.Default)

    private fun activitiesPath(activity: LocationActivity, type: String) = "activities/${activity.familyId}/$type/${activity.id}"
}