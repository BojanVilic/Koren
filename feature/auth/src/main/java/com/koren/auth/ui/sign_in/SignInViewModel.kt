package com.koren.auth.ui.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.data.services.AuthService
import com.koren.data.services.SignInMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authService: AuthService
): ViewModel() {

    private val _state = MutableStateFlow<SignInUiState>(SignInUiState.Shown(eventSink = ::handleEvent))
    val state: StateFlow<SignInUiState> = _state.asStateFlow()

    private fun handleEvent(event: SignInEvent) {
        withShownState { current ->
            when (event) {
                is SignInEvent.GoogleSignIn -> googleSignIn(current)
                is SignInEvent.EmailChanged -> _state.update { current.copy(email = event.email) }
                is SignInEvent.PasswordChanged -> _state.update { current.copy(password = event.password) }
                is SignInEvent.ShowPasswordClicked -> _state.update { current.copy(showPassword = !current.showPassword) }
                is SignInEvent.ClearErrorMessage -> _state.update { current.copy(errorMessage = "") }
                is SignInEvent.SignInClicked -> signIn(current)
            }
        }
    }

    private fun signIn(current: SignInUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signIn(SignInMethod.Email(current.email, current.password))
                .onSuccess { _state.update { SignInUiState.NavigateToHome } }
                .onFailure { error -> _state.update { current.copy(errorMessage = error.message?: "Unknown error.") } }
        }
    }

    private fun googleSignIn(current: SignInUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signIn(SignInMethod.Google)
                .onSuccess { _state.update { SignInUiState.NavigateToHome } }
                .onFailure { error -> _state.update { current.copy(errorMessage = error.message?: "Unknown error.") } }
        }
    }

    private inline fun withShownState(action: (SignInUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is SignInUiState.Shown) {
            action(currentState)
        }
    }
}