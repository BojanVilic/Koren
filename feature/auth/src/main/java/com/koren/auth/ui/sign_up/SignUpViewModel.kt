package com.koren.auth.ui.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.koren.auth.domain.UpdateUserDataOnSignUpUseCase
import com.koren.auth.service.EmailAuthService.Companion.parsePasswordRequirements
import com.koren.data.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val updateUserDataOnSignUpUseCase: UpdateUserDataOnSignUpUseCase
): ViewModel() {

    private val _state = MutableStateFlow<SignUpUiState>(SignUpUiState.Shown(eventSink = ::handleEvent))
    val state: StateFlow<SignUpUiState> = _state.asStateFlow()

    private fun handleEvent(event: SignUpEvent) {
        withShownState { current ->
            when (event) {
                is SignUpEvent.SignUpButtonClicked -> emailSignUp(current)
                is SignUpEvent.FirstNameChanged -> _state.update { current.copy(firstName = event.firstName) }
                is SignUpEvent.LastNameChanged -> _state.update { current.copy(lastName = event.lastName) }
                is SignUpEvent.EmailChanged -> _state.update { current.copy(email = event.email) }
                is SignUpEvent.PasswordChanged -> _state.update { current.copy(password = event.password) }
                is SignUpEvent.SetImageUri -> _state.update { current.copy(imageUri = event.imageUri) }
                is SignUpEvent.ShowPasswordClicked -> _state.update { current.copy(showPassword = !current.showPassword) }
                is SignUpEvent.SignInClicked -> _state.update { SignUpUiState.NavigateBack }
            }
        }
    }

    private fun emailSignUp(
        current: SignUpUiState.Shown
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signUp(current.email, current.password) { result ->
                result
                    .onSuccess {
                        viewModelScope.launch(Dispatchers.Default) {
                            updateUserDataOnSignUpUseCase(current.displayName, current.imageUri)
                        }
                        _state.update { SignUpUiState.NavigateToHome }
                    }
                    .onFailure { error ->
                        when (error) {
                            is FirebaseAuthInvalidCredentialsException -> _state.update { current.copy(emailErrorMessage = error.message?: "Unknown error") }
                            is FirebaseException -> _state.update { current.copy(passwordErrorMessage = error.parsePasswordRequirements().joinToString("\n"), emailErrorMessage = "", genericErrorMessage = "") }
                            else -> _state.update { current.copy(genericErrorMessage = error.message?: "Unknown error", emailErrorMessage = "", passwordErrorMessage = "") }
                        }
                    }
            }
        }
    }

    private inline fun withShownState(action: (SignUpUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is SignUpUiState.Shown) {
            action(currentState)
        }
    }
}