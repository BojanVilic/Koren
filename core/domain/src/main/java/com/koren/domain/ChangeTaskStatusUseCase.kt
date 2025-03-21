package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ChangeTaskStatusUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(taskId: String, isCompleted: Boolean) {
        val user = userSession.currentUser.first()
        val ref = firebaseDatabase.reference.child("families/${user.familyId}/tasks/${taskId}/completed")
        ref.setValue(isCompleted)
    }
}