package com.koren.map.service

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationUpdateScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicUpdates(frequency: Long) {
        val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(frequency, TimeUnit.MINUTES).build()

        workManager.enqueueUniquePeriodicWork(
            "locationUpdateWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}