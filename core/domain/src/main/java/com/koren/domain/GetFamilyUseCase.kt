package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.koren.common.models.family.Family
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.database.getValue
import com.koren.common.models.family.SavedLocation

class GetFamilyUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(): Result<Family> {
        val familyId = userSession.currentUser.first().familyId

        if (familyId.isBlank()) return Result.failure(Exception("Family not found. \uD83D\uDE22"))

        return try {
            val familyRef = firebaseDatabase.getReference("families/$familyId")
            val snapshot = familyRef.get().await()

            if (snapshot.exists()) {
                val id = snapshot.child("id").getValue<String>()?: ""
                val name = snapshot.child("name").getValue<String>()?: ""
                val familyPortrait = snapshot.child("familyPortrait").getValue<String>()?: ""
                val members = snapshot.child("members").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                val savedLocationMap = snapshot.child("savedLocations").getValue(object : GenericTypeIndicator<Map<String, SavedLocation>>() {}) ?: emptyMap()

                val savedLocations = savedLocationMap.values.toList()

                val family = Family(id, name, members, familyPortrait, savedLocations)
                Result.success(family)
            } else {
                Result.failure(Exception("Family not found. \uD83D\uDE22"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}