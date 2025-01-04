package com.koren.home.ui

import com.koren.common.models.Invitation

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Shown(
        val receivedInvitations: List<Invitation> = emptyList(),
        val sentInvitations: List<Invitation> = emptyList(),
        val invitationCodeText: String = "",
        val invitationCodeError: String = "",
        val eventSink: (HomeEvent) -> Unit
    ) : HomeUiState
}

sealed interface HomeEvent {
    data class AcceptInvitation(
        val invitation: Invitation,
        val typedCode: String
    ) : HomeEvent

    data class DeclineInvitation(val id: String) : HomeEvent
    data class InvitationCodeChanged(val code: String) : HomeEvent
}