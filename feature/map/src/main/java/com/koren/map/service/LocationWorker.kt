package com.koren.map.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.koren.common.services.LocationService
import com.koren.common.services.ResourceProvider
import com.koren.data.repository.ActivityRepository
import com.koren.domain.UpdateUserLocationUseCase
import com.koren.map.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltWorker
class LocationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val resourceProvider: ResourceProvider,
    @Assisted private val locationService: LocationService,
    @Assisted private val updateUserLocationUseCase: UpdateUserLocationUseCase,
    @Assisted private val activityRepository: ActivityRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            locationService.updateLocationOnce().let { location ->
                showNotification()
                updateUserLocationUseCase(location)
                activityRepository.insertNewActivity(location)
                dismissNotification()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification() {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setContentTitle(resourceProvider[R.string.notification_location_update_title])
            .setContentText(resourceProvider[R.string.notification_location_update_message])
            .setSmallIcon(com.koren.designsystem.R.drawable.koren_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Updates",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private suspend fun dismissNotification() {
        delay(1000L)
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val CHANNEL_ID = "location_channel"
        private const val NOTIFICATION_ID = 1
    }
}

class LocationWorkerFactory @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val locationService: LocationService,
    private val updateUserLocationUseCase: UpdateUserLocationUseCase,
    private val activityRepository: ActivityRepository
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = LocationWorker(
        appContext = appContext,
        workerParams = workerParameters,
        resourceProvider = resourceProvider,
        locationService = locationService,
        updateUserLocationUseCase = updateUserLocationUseCase,
        activityRepository = activityRepository
    )
}