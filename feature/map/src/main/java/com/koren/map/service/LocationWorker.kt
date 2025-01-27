package com.koren.map.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.koren.common.models.invitation.toRelativeTime
import com.koren.map.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class LocationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Timber.d("PROBAVANJE: LocationWorker pozvan at ${System.currentTimeMillis().toRelativeTime()}")

        displayNotification("Location Update", "LocationWorker is running")


        return Result.success()
    }

    private fun displayNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(applicationContext, "location_updates")
            .setSmallIcon(R.drawable.koren_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkSelfPermission(applicationContext, POST_NOTIFICATIONS)
            }
            notify(1, builder.build())
        }
    }
}
