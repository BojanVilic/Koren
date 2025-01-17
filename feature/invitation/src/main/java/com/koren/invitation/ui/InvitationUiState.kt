package com.koren.invitation.ui

import com.koren.common.models.invitation.InvitationResult
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState

sealed interface InvitationUiState : UiState {
    data class Error(val errorMessage: String): InvitationUiState

    data class Shown(
        val familyName: String = "",
        val isCreateQRInvitationExpanded: Boolean = false,
        val emailInviteText: String = "",
        val isEmailInviteExpanded: Boolean = false,
        val qrInvitation: InvitationResult? = null,
        val emailInvitation: InvitationResult? = null,
        val emailInvitationLoading: Boolean = false,
        val qrInvitationLoading: Boolean = false,
        val errorMessage: String = "",
        override val eventSink: (InvitationEvent) -> Unit
    ): InvitationUiState, EventHandler<InvitationEvent> {
        val isEmailInviteButtonEnabled: Boolean
            get() = emailInviteText.isNotBlank()
    }
}

sealed interface InvitationEvent : UiEvent {
    data object CreateQRInvitation : InvitationEvent
    data object CollapseCreateQRInvitation : InvitationEvent
    data object InviteViaEmailClick : InvitationEvent
    data class EmailInviteTextChange(val email: String) : InvitationEvent
    data object EmailInviteClick : InvitationEvent
}