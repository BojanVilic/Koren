package com.koren.auth.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.koren.auth.service.GoogleAuthService
import com.koren.common.services.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleAuthService: GoogleAuthService,
    resourceProvider: ResourceProvider
): ViewModel() {

    suspend fun signInWithIntent(data: Intent?) {
        data?.let {
            googleAuthService.signInWithIntent(it)
        }
    }

    suspend fun getIntentSender() = googleAuthService.signIn()

}