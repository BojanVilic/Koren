package com.koren.auth.ui.sign_in

import com.koren.common.util.UiEvent
import com.koren.common.util.EventHandler
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface SignInUiState : UiState {
    data class Shown(
        val errorMessage: String = "",
        val email: String = "",
        val password: String = "",
        val showPassword: Boolean = false,
        override val eventSink: (SignInUiEvent) -> Unit
    ) : SignInUiState, EventHandler<SignInUiEvent> {
        val isSignInButtonEnabled: Boolean
            get() = email.isNotBlank() && password.isNotBlank()
    }
}

sealed interface SignInUiEvent : UiEvent {
    data class EmailChanged(val email: String) : SignInUiEvent
    data class PasswordChanged(val password: String) : SignInUiEvent
    data object ShowPasswordClicked : SignInUiEvent
    data object SignInClicked : SignInUiEvent
    data object GoogleSignIn : SignInUiEvent
    data object ClearErrorMessage : SignInUiEvent
    data object NavigateToSignUp : SignInUiEvent
}

sealed interface SignInUiSideEffect : UiSideEffect {
    data class ShowError(val message: String) : SignInUiSideEffect
    data object NavigateToHome : SignInUiSideEffect
    data object NavigateToSignUp : SignInUiSideEffect
}