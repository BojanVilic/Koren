package com.koren.map.service

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationUpdateScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicUpdates() {
        val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(
            15, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "locationUpdateWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}