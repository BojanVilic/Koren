package com.koren.onboarding.ui.join_family

import com.koren.common.models.invitation.Invitation
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface JoinFamilyUiState : UiState {
    data object Loading : JoinFamilyUiState
    data class NoInvitations(val userEmail: String): JoinFamilyUiState
    data class Shown(
        val receivedInvitations: List<Invitation> = emptyList(),
        val invitationCodeText: String = "",
        val invitationCodeError: String = "",
        override val eventSink: (JoinFamilyUiEvent) -> Unit
    ) : JoinFamilyUiState, EventHandler<JoinFamilyUiEvent>
}

sealed interface JoinFamilyUiEvent : UiEvent {
    data class AcceptInvitation(
        val invitation: Invitation,
        val typedCode: String
    ) : JoinFamilyUiEvent
    data class DeclineInvitation(val id: String) : JoinFamilyUiEvent
    data class InvitationCodeChanged(val code: String) : JoinFamilyUiEvent
}

sealed interface JoinFamilyUiSideEffect : UiSideEffect {
    data object NavigateToHome : JoinFamilyUiSideEffect
}