package com.koren.auth.ui.sign_up

import android.net.Uri

sealed interface SignUpUiState {
    data object NavigateToHome : SignUpUiState
    data class Shown(
        val firstName: String = "",
        val lastName: String = "",
        val imageUri: Uri? = null,
        val email: String = "",
        val password: String = "",
        val emailErrorMessage: String = "",
        val passwordErrorMessage: String = "",
        val genericErrorMessage: String = "",
        val showPassword: Boolean = false,
        val eventSink: (SignUpEvent) -> Unit
    ): SignUpUiState {
        val isSignUpButtonEnabled: Boolean
            get() =
                email.isNotEmpty() &&
                password.isNotEmpty() &&
                firstName.isNotEmpty() &&
                lastName.isNotEmpty()

        val displayName: String
            get() = "$firstName $lastName"
    }
}

sealed interface SignUpEvent {
    data object SignUpButtonClicked : SignUpEvent
    data class FirstNameChanged(val firstName: String) : SignUpEvent
    data class LastNameChanged(val lastName: String) : SignUpEvent
    data class EmailChanged(val email: String) : SignUpEvent
    data class PasswordChanged(val password: String) : SignUpEvent
    data class SetImageUri(val imageUri: Uri?) : SignUpEvent
}