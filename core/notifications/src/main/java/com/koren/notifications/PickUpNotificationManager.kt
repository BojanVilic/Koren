package com.koren.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PickUpNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pick Up Requests",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Notifications for pick up requests"
        notificationManager.createNotificationChannel(channel)
    }

    fun showPickUpRequestNotification() {
        // Hardcoded location (example: Central Park, NYC)
        val destinationLatLng = "40.7812,-73.9665"

        // Create intent for Google Maps directions
        val mapIntent = Intent(Intent.ACTION_VIEW, "google.navigation:q=$destinationLatLng&mode=d".toUri())
        mapIntent.setPackage("com.google.android.apps.maps")

        // Create pending intent for the navigation action
        val mapPendingIntent = PendingIntent.getActivity(
            context,
            0,
            mapIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Create intent to open the app
        val openAppIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            1,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.koren.designsystem.R.drawable.koren_icon)
            .setContentTitle("Pick Up Request")
            .setContentText("Your family member needs a ride. Tap to see details.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                android.R.drawable.ic_dialog_map,
                "Navigate",
                mapPendingIntent
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "pickup_request_channel"
        private const val NOTIFICATION_ID = 101
    }
}