package com.koren.home.ui.qr

import com.koren.common.models.Invitation
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface QRInvitationUiState : UiState {
    data class Shown(
        val invitation: Invitation,
        override val eventSink: (QRInvitationUiEvent) -> Unit
    ) : QRInvitationUiState, EventHandler<QRInvitationUiEvent>
    data object Loading : QRInvitationUiState
}

sealed interface QRInvitationUiEvent : UiEvent {
    data class AcceptInvitation(val qrInvCode: String) : QRInvitationUiEvent
    data object DeclineInvitation : QRInvitationUiEvent
}

sealed interface QRInvitationSideEffect : UiSideEffect {
    data object NavigateToHome : QRInvitationSideEffect
    data class NavigateToHomeWithError(val errorMessage: String) : QRInvitationSideEffect
}