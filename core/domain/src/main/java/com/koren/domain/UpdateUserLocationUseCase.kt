package com.koren.domain

import android.location.Location
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.user.UserLocation
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateUserLocationUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(location: Location) {
        val user = userSession.currentUser.first()

        firebaseDatabase.reference.child("users/${user.id}")
            .setValue(user.copy(lastLocation = UserLocation(location.latitude, location.longitude)))
            .await()
    }
}