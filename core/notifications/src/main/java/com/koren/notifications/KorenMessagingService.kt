package com.koren.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.koren.domain.UpdateUserFCMTokenUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class KorenMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var updateUserFCMTokenUseCase: UpdateUserFCMTokenUseCase

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New FCM token received: $token")

        serviceScope.launch {
            updateUserFCMTokenUseCase.invoke(token)
                .onSuccess { Timber.d("Successfully processed new FCM token.") }
                .onFailure { Timber.e(it, "Failed to process new FCM token.") }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("Message received From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Timber.d("Message Notification Title: ${it.title}")
            Timber.d("Message Notification Body: ${it.body}")
        }

        remoteMessage.data.isNotEmpty().let {
            Timber.d("Message data payload: ${remoteMessage.data}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Timber.d("KorenMessagingService destroyed, coroutine scope cancelled.")
    }
}