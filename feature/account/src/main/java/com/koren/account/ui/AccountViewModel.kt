package com.koren.account.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.data.services.AuthService
import com.koren.domain.UploadProfilePictureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
    private val userSession: UserSession,
    private val authService: AuthService
): ViewModel() {

    private val _state = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val state: StateFlow<AccountUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userSession.currentUser.collect { user ->
                _state.update { AccountUiState.Shown(userData = user, eventSink = ::handleEvent) }
            }
        }
    }

    private fun handleEvent(event: AccountUiEvent) {
        withShownState { currentState ->
            when (event) {
                is AccountUiEvent.UploadNewProfilePicture -> uploadProfilePicture(currentState, event.uri)
                is AccountUiEvent.LogOut -> signOut()
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signOut()
        }
    }

    private fun uploadProfilePicture(currentState: AccountUiState.Shown, pictureUri: Uri?) {
        if (currentState.userData?.id == null || pictureUri == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val result = uploadProfilePictureUseCase(currentState.userData.id, pictureUri)
            if (result.isFailure) {
                _state.update { currentState.copy(errorMessage = result.exceptionOrNull()?.message?: "") }
            }
        }
    }

    private inline fun withShownState(action: (AccountUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is AccountUiState.Shown) {
            action(currentState)
        }
    }
}