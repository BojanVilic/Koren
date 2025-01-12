package com.koren.auth.ui.sign_up

import android.net.Uri
import com.koren.common.util.UiEvent
import com.koren.common.util.EventHandler
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface SignUpUiState : UiState {
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
        override val eventSink: (SignUpUiEvent) -> Unit
    ): SignUpUiState, EventHandler<SignUpUiEvent> {
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

sealed interface SignUpUiEvent : UiEvent {
    data object SignUpButtonClicked : SignUpUiEvent
    data class FirstNameChanged(val firstName: String) : SignUpUiEvent
    data class LastNameChanged(val lastName: String) : SignUpUiEvent
    data class EmailChanged(val email: String) : SignUpUiEvent
    data class PasswordChanged(val password: String) : SignUpUiEvent
    data class SetImageUri(val imageUri: Uri) : SignUpUiEvent
    data object ShowPasswordClicked : SignUpUiEvent
    data object SignInClicked : SignUpUiEvent
}

sealed interface SignUpUiSideEffect : UiSideEffect {
    data object NavigateToHome : SignUpUiSideEffect
    data object NavigateBack : SignUpUiSideEffect
    data class ShowGenericMessage(val message: String) : SignUpUiSideEffect
}