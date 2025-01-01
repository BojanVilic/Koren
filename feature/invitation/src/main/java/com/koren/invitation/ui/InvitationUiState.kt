package com.koren.invitation.ui

import com.koren.common.models.InvitationResult

sealed interface InvitationUiState {
    data class Error(val message: String) : InvitationUiState
    data class Idle(
        val isCreateQRInvitationExpanded: Boolean = false,
        val emailInviteText: String = "",
        val isEmailInviteButtonEnabled: Boolean = false,
        val isEmailInviteExpanded: Boolean = false,
        val qrInvitation: InvitationResult? = null,
        val emailInvitation: InvitationResult? = null,
        val emailInvitationLoading: Boolean = false,
        val qrInvitationLoading: Boolean = false,
        val eventSink: (InvitationEvent) -> Unit
    ) : InvitationUiState
}

sealed interface InvitationEvent {
    data object CreateQRInvitation : InvitationEvent
    data object CollapseCreateQRInvitation : InvitationEvent
    data object InviteViaEmailClick : InvitationEvent
    data class EmailInviteTextChange(val email: String) : InvitationEvent
    data object EmailInviteClick : InvitationEvent
}