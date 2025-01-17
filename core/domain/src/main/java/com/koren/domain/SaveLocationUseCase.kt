package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.family.SavedLocation
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(location: SavedLocation): Result<Unit> {
        val user = userSession.currentUser.first()

        try {
            firebaseDatabase.reference.child("families/${user.familyId}/saved_locations/${location.id}")
                .setValue(location)
                .await()
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}