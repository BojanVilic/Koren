package com.koren.account.ui.account

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.koren.account.ui.account.AreYouSureDialogType.LeaveFamily
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

        if (userData == UserData()) return AccountUiState.Loading

        var areYouSureDialogType by remember { mutableStateOf<AreYouSureDialogType>(AreYouSureDialogType.None) }
        var areYouSureActionInProgress by remember { mutableStateOf(false) }

        return AccountUiState.Shown(
            userData = userData,
            appVersion = appInfoProvider.getAppVersion(),
            areYouSureDialogType = areYouSureDialogType,
            areYouSureActionInProgress = areYouSureActionInProgress
        ) { event ->
            when (event) {
                is AccountUiEvent.UploadNewProfilePicture -> uploadProfilePicture(userData.id, event.uri)
                is AccountUiEvent.EditProfile -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToEditProfile)
                is AccountUiEvent.ChangePassword -> sendSideEffect(AccountUiSideEffect.NavigateToChangePassword)
                is AccountUiEvent.LogOut -> signOut()
                is AccountUiEvent.LeaveFamily -> areYouSureDialogType = LeaveFamily(userData.id)
                is AccountUiEvent.DeleteFamily -> areYouSureDialogType = AreYouSureDialogType.DeleteFamilyMember(userData.id)
                is AccountUiEvent.DeleteAccount -> areYouSureDialogType = AreYouSureDialogType.DeleteAccount(userData.id)
                is AccountUiEvent.SendFeedback -> Unit
                is AccountUiEvent.Notifications -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToNotifications)
                is AccountUiEvent.TermsOfService -> Unit
                is AccountUiEvent.Privacy -> Unit
                is AccountUiEvent.Activity -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToActivity)
                is AccountUiEvent.Premium -> Unit
                is AccountUiEvent.ManageFamily -> _sideEffects.emitSuspended(AccountUiSideEffect.NavigateToManageFamily)
                is AccountUiEvent.ConfirmAreYouSureDialog -> {
                    areYouSureActionInProgress = true
                    confirmAreYouSureAction(
                        dialogType = areYouSureDialogType,
                        dismissInProgressState = {
                            areYouSureActionInProgress = false
                            areYouSureDialogType = AreYouSureDialogType.None
                        }
                    )
                }
                is AccountUiEvent.DismissAreYouSureDialog -> areYouSureDialogType = AreYouSureDialogType.None
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

    private fun leaveFamily(
        memberId: String,
        dismissInProgressState: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            removeMemberFromFamilyUseCase(memberId)
                .onSuccess {
                    _sideEffects.emitSuspended(AccountUiSideEffect.ShowMessage("You have successfully left the family."))
                    dismissInProgressState()
                }
                .onFailure { error ->
                    _sideEffects.emitSuspended(AccountUiSideEffect.ShowMessage(message = error.message.orUnknownError()))
                    dismissInProgressState()
                }
        }
    }

    private fun confirmAreYouSureAction(
        dialogType: AreYouSureDialogType,
        dismissInProgressState: () -> Unit
    ) {
        when (dialogType) {
            is AreYouSureDialogType.LeaveFamily -> leaveFamily(dialogType.userId, dismissInProgressState)
            is AreYouSureDialogType.DeleteFamilyMember -> sendSideEffect(AccountUiSideEffect.ShowMessage("This feature is not implemented yet."))
            is AreYouSureDialogType.DeleteAccount -> sendSideEffect(AccountUiSideEffect.ShowMessage("This feature is not implemented yet."))
            is AreYouSureDialogType.None -> Unit
        }
    }
}