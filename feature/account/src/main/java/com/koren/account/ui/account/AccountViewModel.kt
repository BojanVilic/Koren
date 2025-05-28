package com.koren.account.ui.account

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.koren.account.ui.change_password.ChangePasswordScreen
import com.koren.common.services.UserSession
import com.koren.common.services.app_info.AppInfoProvider
import com.koren.common.util.StateViewModel
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
    private val authService: AuthService,
    private val appInfoProvider: AppInfoProvider
): StateViewModel<AccountUiEvent, AccountUiState, AccountUiSideEffect>() {

    override fun setInitialState(): AccountUiState = AccountUiState.Loading

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            userSession.currentUser.collect { user ->
                _uiState.update {
                    AccountUiState.Shown(
                        userData = user,
                        appVersion = appInfoProvider.getAppVersion(),
                        eventSink = ::handleEvent
                    )
                }
            }
        }
    }

    override fun handleEvent(event: AccountUiEvent) {
        withEventfulState<AccountUiState.Shown> { currentState ->
            when (event) {
                is AccountUiEvent.UploadNewProfilePicture -> uploadProfilePicture(currentState, event.uri)
                is AccountUiEvent.EditProfile -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToEditProfile)
                is AccountUiEvent.ChangePassword -> _uiState.update { currentState.copy(optionContent = { ChangePasswordScreen(onDismissRequest = { currentState.eventSink(AccountUiEvent.CloseOption) }) }) }
                is AccountUiEvent.LogOut -> signOut()
                is AccountUiEvent.DeleteAccount -> Unit
                is AccountUiEvent.LeaveFamily -> Unit
                is AccountUiEvent.SendFeedback -> Unit
                is AccountUiEvent.Notifications -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToNotifications)
                is AccountUiEvent.TermsOfService -> Unit
                is AccountUiEvent.Privacy -> Unit
                is AccountUiEvent.Activity -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToActivity)
                is AccountUiEvent.Premium -> Unit
                is AccountUiEvent.CloseOption -> _uiState.update { currentState.copy(optionContent = null) }
                is AccountUiEvent.ManageFamily -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToManageFamily)
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signOut()
                .onSuccess { _sideEffects.emitSuspended(AccountUiSideEffect.LogOut) }
                .onFailure { error -> _sideEffects.emitSuspended(
                    AccountUiSideEffect.ShowError(
                        message = error.message.orUnknownError()
                    )
                ) }
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