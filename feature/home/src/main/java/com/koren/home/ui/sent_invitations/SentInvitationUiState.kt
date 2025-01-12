package com.koren.home.ui.sent_invitations

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState

sealed interface SentInvitationUiState : UiState {
    data object Loading : SentInvitationUiState
    data class Error(val message: String) : SentInvitationUiState
    data object Empty : SentInvitationUiState

    data class Shown(
        val sentInvitations: List<UiSentInvitation> = emptyList(),
        override val eventSink: (SentInvitationEvent) -> Unit
    ) : SentInvitationUiState, EventHandler<SentInvitationEvent>
}

sealed interface SentInvitationEvent : UiEvent {
    data class ExpandQRCode(val id: String) : SentInvitationEvent
}