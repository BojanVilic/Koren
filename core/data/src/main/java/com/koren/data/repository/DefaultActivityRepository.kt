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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DefaultActivityRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) : ActivityRepository {

    companion object {
        const val LOCATION_ACTIVITY = "location"
    }

    override suspend fun insertNewActivity(activity: LocationActivity) {
        firebaseDatabase.getReference(activitiesPath(activity, LOCATION_ACTIVITY))
            .setValue(activity)
            .await()
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