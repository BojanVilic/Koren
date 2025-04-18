package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class RemoveCallHomeRequestUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke() {
        try {
            val user = userSession.currentUser.first()
            firebaseDatabase.reference.child("families/${user.familyId}/callHomeRequests/${user.id}")
                .removeValue()
                .await()
        } catch (e: Exception) {
            Timber.e("Error removing call home request: ${e.message}")
        }
    }
}