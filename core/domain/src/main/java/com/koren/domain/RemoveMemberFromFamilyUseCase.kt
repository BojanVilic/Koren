package com.koren.domain

import com.google.firebase.functions.FirebaseFunctions
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoveMemberFromFamilyUseCase @Inject constructor(
    private val firebaseFunctions: FirebaseFunctions,
    private val userSession: UserSession
) {

    companion object {
        const val REMOVE_MEMBER_FUNCTION = "removeUserFromFamily"
    }

    suspend operator fun invoke(memberId: String): Result<Unit> {
        return try {
            val familyId = userSession.currentUser.first().familyId

            val data = mapOf(
                "familyIdToRemoveFrom" to familyId,
                "userIdToRemove" to memberId
            )

            firebaseFunctions
                .getHttpsCallable(REMOVE_MEMBER_FUNCTION)
                .call(data)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}