package com.koren.map.service

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.koren.common.services.LocationService
import com.koren.common.services.ResourceProvider
import com.koren.data.repository.ActivityRepository
import com.koren.domain.UpdateUserLocationUseCase
import com.koren.map.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class LocationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val resourceProvider: ResourceProvider,
//    private val locationService: LocationService,
//    private val updateUserLocationUseCase: UpdateUserLocationUseCase,
//    private val activityRepository: ActivityRepository
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {
            Timber.d("PROBAVANJE: worker successfully started")
//            val location = locationService.updateLocationOnce()
//            updateUserLocationUseCase(location)
//            activityRepository.insertNewActivity(location)
            displayNotification()
            return Result.success()
        } catch (e: Exception) {
            Timber.d("PROBAVANJE: worker failed")
            return Result.failure()
        }
    }

    private fun displayNotification() {
        val builder = NotificationCompat.Builder(appContext, "location_updates")
            .setSmallIcon(R.drawable.koren_icon)
            .setContentTitle(resourceProvider[R.string.notification_location_update_title])
            .setContentText(resourceProvider[R.string.notification_location_update_message])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(appContext)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkSelfPermission(appContext, POST_NOTIFICATIONS)
            }
            notify(1, builder.build())
        }
    }
}
