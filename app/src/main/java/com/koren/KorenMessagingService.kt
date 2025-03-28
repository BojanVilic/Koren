package com.koren

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class KorenMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle the new token as needed
        // For example, send it to your server or save it locally
        Timber.d("New FCM token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle incoming messages here
        Timber.d("Message received: ${remoteMessage.data}")
    }
}