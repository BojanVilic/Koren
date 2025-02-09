package com.koren.account.ui.change_password

import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.data.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userSession: UserSession,
    private val authService: AuthService
): StateViewModel<ChangePasswordUiEvent, ChangePasswordUiState, ChangePasswordSideEffect>() {

    override fun setInitialState(): ChangePasswordUiState = ChangePasswordUiState.Loading

    fun init() {
        viewModelScope.launch(Dispatchers.Default) {
            val email = userSession.currentUser.first().email
            _uiState.update {
                ChangePasswordUiState.Shown(
                    email = email,
                    eventSink = ::handleEvent
                )
            }
        }
    }

    override fun handleEvent(event: ChangePasswordUiEvent) {
        withEventfulState<ChangePasswordUiState.Shown> { currentState ->
            when (event) {
                is ChangePasswordUiEvent.Close -> _sideEffects.emitSuspended(ChangePasswordSideEffect.Close)
                is ChangePasswordUiEvent.SendResetPasswordEmail -> resetPassword(currentState)
                is ChangePasswordUiEvent.EmailChanged -> _uiState.update { currentState.copy(email = event.email) }
            }
        }
    }

    private fun resetPassword(currentState: ChangePasswordUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            authService.resetPassword(currentState.email)
            _uiState.update { ChangePasswordUiState.EmailSent(
                email = currentState.email,
                close = { _sideEffects.emitSuspended(ChangePasswordSideEffect.Close) }
            ) }
        }
    }
}