package com.koren.invitation.ui

import com.koren.common.models.InvitationResult

sealed interface InvitationUiState {
    data class InvitationCreated(
        val invitationResult: InvitationResult
    ) : InvitationUiState
    data object Error : InvitationUiState
    data object Loading : InvitationUiState
    data class Idle(
        val eventSink: (InvitationEvent) -> Unit
    ) : InvitationUiState
}

sealed interface InvitationEvent {
    data object CreateInvitation : InvitationEvent
}