package com.koren.account.ui

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState
import com.koren.common.util.orUnknownError
import com.koren.data.services.AuthService
import com.koren.domain.UploadProfilePictureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
    private val userSession: UserSession,
    private val authService: AuthService
): StateViewModel<AccountUiEvent, AccountUiState, AccountUiSideEffect>() {

    override fun setInitialState(): AccountUiState = AccountUiState.Loading

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userSession.currentUser.collect { user ->
                _uiState.update { AccountUiState.Shown(userData = user, eventSink = ::handleEvent) }
            }
        }
    }

    override fun handleEvent(event: AccountUiEvent) {
        withEventfulState<AccountUiState.Shown> { currentState ->
            when (event) {
                is AccountUiEvent.UploadNewProfilePicture -> uploadProfilePicture(currentState, event.uri)
                is AccountUiEvent.LogOut -> signOut()
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signOut()
                .onSuccess { _sideEffects.emitSuspended(AccountUiSideEffect.LogOut) }
                .onFailure { error -> _sideEffects.emitSuspended(AccountUiSideEffect.ShowError(message = error.message.orUnknownError())) }
        }
    }

    private fun uploadProfilePicture(currentState: AccountUiState.Shown, pictureUri: Uri?) {
        if (currentState.userData?.id == null || pictureUri == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val result = uploadProfilePictureUseCase(currentState.userData.id, pictureUri)
            if (result.isFailure) {
                _sideEffects.emitSuspended(AccountUiSideEffect.ShowError(message = result.exceptionOrNull()?.message.orUnknownError()))
            }
        }
    }
}