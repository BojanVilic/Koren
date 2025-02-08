package com.koren.account.ui.edit_profile

import android.net.Uri
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface EditProfileUiState : UiState {
    data object Loading : EditProfileUiState
    data class Shown(
        val userData: UserData? = null,
        val newProfilePicture: Uri? = null,
        override val eventSink: (EditProfileUiEvent) -> Unit
    ) : EditProfileUiState, EventHandler<EditProfileUiEvent>
}

sealed interface EditProfileUiEvent : UiEvent {
    data class UploadNewProfilePicture(val uri: Uri?) : EditProfileUiEvent
    data class OnNameChange(val name: String) : EditProfileUiEvent
    data object SaveProfile : EditProfileUiEvent
}

sealed interface EditProfileSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : EditProfileSideEffect
}