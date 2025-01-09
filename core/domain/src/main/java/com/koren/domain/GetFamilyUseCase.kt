package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.Family
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.database.getValue

class GetFamilyUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(): Result<Family> {
        val familyId = userSession.currentUser.first().familyId

        val family = firebaseDatabase.getReference("families/$familyId")
            .get()
            .await()
            .getValue<Family>()?: return Result.failure(Exception("Family not found. \uD83D\uDE22"))
        return Result.success(family)
    }
}