package com.koren.account.ui.account

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.services.app_info.AppInfoProvider
import com.koren.common.util.MoleculeViewModel
import com.koren.common.util.orUnknownError
import com.koren.data.services.AuthService
import com.koren.domain.RemoveMemberFromFamilyUseCase
import com.koren.domain.UploadProfilePictureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
    private val userSession: UserSession,
    private val authService: AuthService,
    private val appInfoProvider: AppInfoProvider,
    private val removeMemberFromFamilyUseCase: RemoveMemberFromFamilyUseCase,
): MoleculeViewModel<AccountUiEvent, AccountUiState, AccountUiSideEffect>() {

    override fun setInitialState(): AccountUiState = AccountUiState.Loading

    @Composable
    override fun produceState(): AccountUiState {
        val userData by userSession.currentUser.collectAsState(initial = UserData())

        return AccountUiState.Shown(
            userData = userData,
            appVersion = appInfoProvider.getAppVersion()
        ) { event ->
            when (event) {
                is AccountUiEvent.UploadNewProfilePicture -> uploadProfilePicture(userData.id, event.uri)
                is AccountUiEvent.EditProfile -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToEditProfile)
                is AccountUiEvent.ChangePassword -> sendSideEffect(AccountUiSideEffect.NavigateToChangePassword)
                is AccountUiEvent.LogOut -> signOut()
                is AccountUiEvent.DeleteAccount -> Unit
                is AccountUiEvent.LeaveFamily -> leaveFamily(userData.id)
                is AccountUiEvent.SendFeedback -> Unit
                is AccountUiEvent.Notifications -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToNotifications)
                is AccountUiEvent.TermsOfService -> Unit
                is AccountUiEvent.Privacy -> Unit
                is AccountUiEvent.Activity -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToActivity)
                is AccountUiEvent.Premium -> Unit
                is AccountUiEvent.ManageFamily -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToManageFamily)
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch(Dispatchers.Default) {
            authService.signOut()
                .onSuccess { _sideEffects.emitSuspended(AccountUiSideEffect.LogOut) }
                .onFailure { error -> _sideEffects.emitSuspended(
                    AccountUiSideEffect.ShowMessage(
                        message = error.message.orUnknownError()
                    )
                ) }
        }
    }

    private fun uploadProfilePicture(userId: String?, pictureUri: Uri?) {
        if (userId == null || pictureUri == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val result = uploadProfilePictureUseCase(userId, pictureUri)
            if (result.isFailure) {
                _sideEffects.emitSuspended(AccountUiSideEffect.ShowMessage(message = result.exceptionOrNull()?.message.orUnknownError()))
            }
        }
    }

    private fun leaveFamily(memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            removeMemberFromFamilyUseCase(memberId)
                .onSuccess { _sideEffects.emitSuspended(AccountUiSideEffect.ShowMessage("You have successfully left the family.")) }
                .onFailure { error -> _sideEffects.emitSuspended(AccountUiSideEffect.ShowMessage(message = error.message.orUnknownError())) }
        }
    }
}