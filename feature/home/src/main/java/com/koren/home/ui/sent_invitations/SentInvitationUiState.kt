package com.koren.home.ui.sent_invitations

sealed interface SentInvitationUiState {
    data object Loading : SentInvitationUiState
    data class Error(val message: String) : SentInvitationUiState
    data object Empty : SentInvitationUiState

    data class Shown(
        val sentInvitations: List<UiSentInvitation> = emptyList(),
        val eventSink: (SentInvitationEvent) -> Unit
    ) : SentInvitationUiState
}

sealed interface SentInvitationEvent {
    data class ExpandQRCode(val id: String) : SentInvitationEvent
}