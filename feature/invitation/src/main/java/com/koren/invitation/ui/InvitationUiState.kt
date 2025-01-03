package com.koren.invitation.ui

import com.koren.common.models.InvitationResult

data class InvitationUiState(
    val familyName: String = "",
    val isCreateQRInvitationExpanded: Boolean = false,
    val emailInviteText: String = "",
    val isEmailInviteButtonEnabled: Boolean = false,
    val isEmailInviteExpanded: Boolean = false,
    val qrInvitation: InvitationResult? = null,
    val emailInvitation: InvitationResult? = null,
    val emailInvitationLoading: Boolean = false,
    val qrInvitationLoading: Boolean = false,
    val errorMessage: String = "",
    val eventSink: (InvitationEvent) -> Unit
)

sealed interface InvitationEvent {
    data object CreateQRInvitation : InvitationEvent
    data object CollapseCreateQRInvitation : InvitationEvent
    data object InviteViaEmailClick : InvitationEvent
    data class EmailInviteTextChange(val email: String) : InvitationEvent
    data object EmailInviteClick : InvitationEvent
}