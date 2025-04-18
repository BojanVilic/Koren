package com.koren.auth.ui.sign_in

import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.data.services.AuthService
import com.koren.data.services.SignInMethod
import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authService: AuthService,
    private val userSession: UserSession
): StateViewModel<SignInUiEvent, SignInUiState, SignInUiSideEffect>() {

    override fun setInitialState(): SignInUiState = SignInUiState.Shown(eventSink = ::handleEvent)

    override fun handleEvent(event: SignInUiEvent) {
        withEventfulState<SignInUiState.Shown> { current ->
            when (event) {
                is SignInUiEvent.GoogleSignIn -> googleSignIn(current)
                is SignInUiEvent.EmailChanged -> _uiState.update { current.copy(email = event.email) }
                is SignInUiEvent.PasswordChanged -> _uiState.update { current.copy(password = event.password) }
                is SignInUiEvent.ShowPasswordClicked -> _uiState.update { current.copy(showPassword = !current.showPassword) }
                is SignInUiEvent.ClearErrorMessage -> _uiState.update { current.copy(errorMessage = "") }
                is SignInUiEvent.NavigateToSignUp -> _sideEffects.emitSuspended(SignInUiSideEffect.NavigateToSignUp)
                is SignInUiEvent.SignInClicked -> signIn(current)
            }
        }
    }

    private fun signIn(current: SignInUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signIn(SignInMethod.Email(current.email.trim(), current.password.trim()))
                .onSuccess { _sideEffects.emit(SignInUiSideEffect.NavigateToOnboarding) }
                .onFailure { error -> _sideEffects.emit(SignInUiSideEffect.ShowError(message = error.message?: "Unknown error.")) }
        }
    }

    private fun googleSignIn(current: SignInUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signIn(SignInMethod.Google)
                .onSuccess {
                    val user = userSession.currentUser.first()
                    if (user.familyId.isBlank()) _sideEffects.emit(SignInUiSideEffect.NavigateToOnboarding)
                    else _sideEffects.emit(SignInUiSideEffect.NavigateToHome)
                }
                .onFailure { error -> _uiState.update { current.copy(errorMessage = error.message?: "Unknown error.") } }
        }
    }
}