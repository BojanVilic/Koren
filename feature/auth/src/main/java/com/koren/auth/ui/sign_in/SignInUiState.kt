package com.koren.auth.ui.sign_in

import com.koren.common.util.Event
import com.koren.common.util.EventHandler
import com.koren.common.util.SideEffect
import com.koren.common.util.UiState

sealed interface SignInUiState : UiState {
    data class Shown(
        val errorMessage: String = "",
        val email: String = "",
        val password: String = "",
        val showPassword: Boolean = false,
        override val eventSink: (SignInEvent) -> Unit
    ) : SignInUiState, EventHandler<SignInEvent> {
        val isSignInButtonEnabled: Boolean
            get() = email.isNotBlank() && password.isNotBlank()
    }
}

sealed interface SignInEvent : Event {
    data class EmailChanged(val email: String) : SignInEvent
    data class PasswordChanged(val password: String) : SignInEvent
    data object ShowPasswordClicked : SignInEvent
    data object SignInClicked : SignInEvent
    data object GoogleSignIn : SignInEvent
    data object ClearErrorMessage : SignInEvent
    data object NavigateToSignUp : SignInEvent
}

sealed interface SignInSideEffect : SideEffect {
    data class ShowError(val message: String) : SignInSideEffect
    data object NavigateToHome : SignInSideEffect
    data object NavigateToSignUp : SignInSideEffect
}