package com.koren.auth.ui.sign_up

import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.koren.auth.domain.UpdateUserDataOnSignUpUseCase
import com.koren.auth.service.EmailAuthService.Companion.parsePasswordRequirements
import com.koren.common.util.StateViewModel
import com.koren.common.util.orUnknownError
import com.koren.data.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val updateUserDataOnSignUpUseCase: UpdateUserDataOnSignUpUseCase
): StateViewModel<SignUpUiEvent, SignUpUiState, SignUpUiSideEffect>() {

    override fun setInitialState(): SignUpUiState = SignUpUiState.Shown(eventSink = ::handleEvent)

    override fun handleEvent(event: SignUpUiEvent) {
        withEventfulState<SignUpUiState.Shown> { current ->
            when (event) {
                is SignUpUiEvent.SignUpButtonClicked -> emailSignUp(current)
                is SignUpUiEvent.FirstNameChanged -> _uiState.update { current.copy(firstName = event.firstName) }
                is SignUpUiEvent.LastNameChanged -> _uiState.update { current.copy(lastName = event.lastName) }
                is SignUpUiEvent.EmailChanged -> _uiState.update { current.copy(email = event.email) }
                is SignUpUiEvent.PasswordChanged -> _uiState.update { current.copy(password = event.password) }
                is SignUpUiEvent.SetImageUri -> _uiState.update { current.copy(imageUri = event.imageUri) }
                is SignUpUiEvent.ShowPasswordClicked -> _uiState.update { current.copy(showPassword = !current.showPassword) }
                is SignUpUiEvent.SignInClicked -> _sideEffects.emitSuspended(SignUpUiSideEffect.NavigateBack)
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
                        _sideEffects.emitSuspended(SignUpUiSideEffect.NavigateToHome)
                    }
                    .onFailure { error ->
                        when (error) {
                            is FirebaseAuthInvalidCredentialsException -> _uiState.update { current.copy(emailErrorMessage = error.message.orUnknownError()) }
                            is FirebaseAuthUserCollisionException -> _sideEffects.emitSuspended(SignUpUiSideEffect.ShowGenericMessage(error.message?: "Email already in use. Please go to the sign in page and try again."))
                            is FirebaseException -> _uiState.update { current.copy(passwordErrorMessage = error.parsePasswordRequirements().joinToString("\n"), emailErrorMessage = "", genericErrorMessage = "") }
                            else -> _uiState.update { current.copy(genericErrorMessage = error.message.orUnknownError(), emailErrorMessage = "", passwordErrorMessage = "") }
                        }
                    }
            }
        }
    }
}