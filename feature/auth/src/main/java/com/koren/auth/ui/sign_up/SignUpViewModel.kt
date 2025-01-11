package com.koren.auth.ui.sign_up

import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.koren.auth.domain.UpdateUserDataOnSignUpUseCase
import com.koren.auth.service.EmailAuthService.Companion.parsePasswordRequirements
import com.koren.common.util.BaseViewModel
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
): BaseViewModel<SignUpEvent, SignUpUiState, SignUpSideEffect>() {

    override fun setInitialState(): SignUpUiState = SignUpUiState.Shown(eventSink = ::handleEvent)

    override fun handleEvent(event: SignUpEvent) {
        withEventfulState<SignUpUiState.Shown> { current ->
            when (event) {
                is SignUpEvent.SignUpButtonClicked -> emailSignUp(current)
                is SignUpEvent.FirstNameChanged -> _uiState.update { current.copy(firstName = event.firstName) }
                is SignUpEvent.LastNameChanged -> _uiState.update { current.copy(lastName = event.lastName) }
                is SignUpEvent.EmailChanged -> _uiState.update { current.copy(email = event.email) }
                is SignUpEvent.PasswordChanged -> _uiState.update { current.copy(password = event.password) }
                is SignUpEvent.SetImageUri -> _uiState.update { current.copy(imageUri = event.imageUri) }
                is SignUpEvent.ShowPasswordClicked -> _uiState.update { current.copy(showPassword = !current.showPassword) }
                is SignUpEvent.SignInClicked -> _sideEffects.emitSuspended(SignUpSideEffect.NavigateBack)
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
                        _sideEffects.emitSuspended(SignUpSideEffect.NavigateToHome)
                    }
                    .onFailure { error ->
                        when (error) {
                            is FirebaseAuthInvalidCredentialsException -> _uiState.update { current.copy(emailErrorMessage = error.message?: "Unknown error") }
                            is FirebaseAuthUserCollisionException -> _sideEffects.emitSuspended(SignUpSideEffect.ShowGenericMessage(error.message?: "Email already in use. Please go to the sign in page and try again."))
                            is FirebaseException -> _uiState.update { current.copy(passwordErrorMessage = error.parsePasswordRequirements().joinToString("\n"), emailErrorMessage = "", genericErrorMessage = "") }
                            else -> _uiState.update { current.copy(genericErrorMessage = error.message?: "Unknown error", emailErrorMessage = "", passwordErrorMessage = "") }
                        }
                    }
            }
        }
    }
}