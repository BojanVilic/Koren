package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateLastUserActivityUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(activityId: String) {
        val user = userSession.currentUser.first()

        firebaseDatabase.reference.child("users/${user.id}/lastActivityId")
            .setValue(activityId)
            .await()
    }
}