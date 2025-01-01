package com.koren.invitation.ui

import com.koren.common.models.InvitationResult

sealed interface InvitationUiState {
    data object Error : InvitationUiState
    data class Idle(
        val isCreateQRInvitationExpanded: Boolean = false,
        val emailInviteText: String = "",
        val isEmailInviteButtonEnabled: Boolean = false,
        val isEmailInviteExpanded: Boolean = false,
        val eventSink: (InvitationEvent) -> Unit,
        val qrInvitation: InvitationResult? = null,
        val emailInvitation: InvitationResult? = null,
        val loading: Boolean = false
    ) : InvitationUiState
}

sealed interface InvitationEvent {
    data object CreateQRInvitation : InvitationEvent
    data object CollapseCreateQRInvitation : InvitationEvent
    data object InviteViaEmailClick : InvitationEvent
    data class EmailInviteTextChange(val email: String) : InvitationEvent
    data object EmailInviteClick : InvitationEvent
}