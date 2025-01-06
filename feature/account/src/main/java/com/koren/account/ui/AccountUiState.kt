package com.koren.account.ui

import android.net.Uri
import com.koren.common.models.UserData

sealed interface AccountUiState {
    data object Loading : AccountUiState
    data class Shown(
        val userData: UserData? = null,
        val errorMessage: String = "",
        val eventSink: (AccountUiEvent) -> Unit
    ) : AccountUiState
}

sealed interface AccountUiEvent {
    data class UploadNewProfilePicture(val uri: Uri?) : AccountUiEvent
    data object LogOut : AccountUiEvent
}