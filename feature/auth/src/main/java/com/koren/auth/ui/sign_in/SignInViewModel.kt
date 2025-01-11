package com.koren.auth.ui.sign_in

import androidx.lifecycle.viewModelScope
import com.koren.data.services.AuthService
import com.koren.data.services.SignInMethod
import com.koren.common.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authService: AuthService
): BaseViewModel<SignInEvent, SignInUiState, SignInSideEffect>() {

    override fun setInitialState(): SignInUiState = SignInUiState.Shown(eventSink = ::handleEvent)

    override fun handleEvent(event: SignInEvent) {
        withEventfulState<SignInUiState.Shown> { current ->
            when (event) {
                is SignInEvent.GoogleSignIn -> googleSignIn(current)
                is SignInEvent.EmailChanged -> _uiState.update { current.copy(email = event.email) }
                is SignInEvent.PasswordChanged -> _uiState.update { current.copy(password = event.password) }
                is SignInEvent.ShowPasswordClicked -> _uiState.update { current.copy(showPassword = !current.showPassword) }
                is SignInEvent.ClearErrorMessage -> _uiState.update { current.copy(errorMessage = "") }
                is SignInEvent.NavigateToSignUp -> _sideEffects.emitSuspended(SignInSideEffect.NavigateToSignUp)
                is SignInEvent.SignInClicked -> signIn(current)
            }
        }
    }

    private fun signIn(current: SignInUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signIn(SignInMethod.Email(current.email, current.password))
                .onSuccess { _sideEffects.emit(SignInSideEffect.NavigateToHome) }
                .onFailure { error -> _sideEffects.emit(SignInSideEffect.ShowError(message = error.message?: "Unknown error.")) }
        }
    }

    private fun googleSignIn(current: SignInUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signIn(SignInMethod.Google)
                .onSuccess { _sideEffects.emit(SignInSideEffect.NavigateToHome) }
                .onFailure { error -> _uiState.update { current.copy(errorMessage = error.message?: "Unknown error.") } }
        }
    }
}