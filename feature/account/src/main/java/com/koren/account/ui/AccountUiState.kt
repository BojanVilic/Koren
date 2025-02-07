package com.koren.account.ui

import android.net.Uri
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface AccountUiState : UiState {
    data object Loading : AccountUiState
    data class Shown(
        val userData: UserData? = null,
        val appVersion: String = "",
        override val eventSink: (AccountUiEvent) -> Unit
    ) : AccountUiState, EventHandler<AccountUiEvent>
}

sealed interface AccountUiEvent : UiEvent {
    data class UploadNewProfilePicture(val uri: Uri?) : AccountUiEvent
    data object EditProfile : AccountUiEvent
    data object ChangePassword : AccountUiEvent
    data object LogOut : AccountUiEvent
    data object Notifications : AccountUiEvent
    data object TermsOfService : AccountUiEvent
    data object Privacy : AccountUiEvent
    data object LeaveFamily : AccountUiEvent
    data object DeleteAccount : AccountUiEvent
    data object SendFeedback : AccountUiEvent
}

sealed interface AccountUiSideEffect : UiSideEffect {
    data object LogOut : AccountUiSideEffect
    data class ShowError(val message: String) : AccountUiSideEffect
}