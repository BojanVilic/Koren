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
    suspend operator fun invoke(): Family {
        val familyId = userSession.currentUser.first().familyId

        return firebaseDatabase.getReference("families/$familyId")
            .get()
            .await()
            .getValue<Family>()?: throw Exception("Family not found")
    }
}