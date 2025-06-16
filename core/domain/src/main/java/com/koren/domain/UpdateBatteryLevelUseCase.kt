package com.koren.domain

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.BatteryService
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateBatteryLevelUseCase @Inject constructor(
    private val userSession: UserSession,
    private val batteryService: BatteryService,
    private val firebaseDatabase: FirebaseDatabase
) {

    suspend operator fun invoke() {
        val batteryLevel = batteryService.getCurrentBatteryLevel()

        val user = userSession.currentUser.first()

        firebaseDatabase.reference.child("users/${user.id}/batteryLevel")
            .setValue(batteryLevel)
            .await()
    }
}