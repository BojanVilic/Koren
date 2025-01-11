package com.koren.auth.ui.sign_in

sealed interface SignInUiState {
    data object NavigateToHome : SignInUiState

    data class Shown(
        val errorMessage: String = "",
        val email: String = "",
        val password: String = "",
        val showPassword: Boolean = false,
        val eventSink: (SignInEvent) -> Unit
    ) : SignInUiState {
        val isSignInButtonEnabled: Boolean
            get() = email.isNotBlank() && password.isNotBlank()
    }
}

sealed interface SignInEvent {
    data class EmailChanged(val email: String) : SignInEvent
    data class PasswordChanged(val password: String) : SignInEvent
    data object ShowPasswordClicked : SignInEvent
    data object SignInClicked : SignInEvent
    data object GoogleSignIn : SignInEvent
    data object ClearErrorMessage : SignInEvent
}

sealed interface SignInSideEffect {
    data class ShowError(val message: String) : SignInSideEffect
    data object NavigateToHome : SignInSideEffect
}