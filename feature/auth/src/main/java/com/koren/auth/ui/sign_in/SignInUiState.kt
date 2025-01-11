package com.koren.auth.ui.sign_in

sealed interface SignInUiState {
    data object NavigateToHome : SignInUiState

    data class Shown(
        val loading: Boolean = false,
        val errorMessage: String = "",
        val eventSink: (SignInEvent) -> Unit
    ) : SignInUiState
}

sealed interface SignInEvent {
    data object GoogleSignIn : SignInEvent
}