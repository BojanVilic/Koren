package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.koren.common.models.Family
import com.koren.common.models.UserData
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetAllFamilyMembersUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(): List<UserData> {
        val familyId = userSession.currentUser.first().familyId

        val memberIds = firebaseDatabase.getReference("families/$familyId")
            .get()
            .await()
            .getValue<Family>()?.members?: throw Exception("Family not found")

        firebaseDatabase.getReference("users")
            .get()
            .await()
            .children
            .map { it.getValue<UserData>() }
            .filter { it?.id in memberIds }
            .let { return it.filterNotNull() }
    }
}
