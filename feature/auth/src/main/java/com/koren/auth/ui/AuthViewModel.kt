package com.koren.auth.ui

import androidx.lifecycle.ViewModel
import com.koren.data.services.AuthService
import com.koren.data.services.SignInMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
): ViewModel() {

    suspend fun signIn() = authService.signIn(SignInMethod.GOOGLE)
}