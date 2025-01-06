package com.koren.home.ui.qr

import com.koren.common.models.Invitation

sealed interface QRInvitationUiState {
    data class NavigateToHome(
        val errorMessage: String = ""
    ) : QRInvitationUiState
    data class Shown(
        val invitation: Invitation,
        val eventSink: (QRInvitationUiEvent) -> Unit
    ) : QRInvitationUiState
    data object Loading : QRInvitationUiState
}

sealed interface QRInvitationUiEvent {
    data class AcceptInvitation(val qrInvCode: String) : QRInvitationUiEvent
    data object DeclineInvitation : QRInvitationUiEvent
}