package com.koren.home.ui.home

import com.koren.common.models.invitation.Invitation
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface HomeUiState : UiState {
    data object Loading : HomeUiState
    data class Shown(
        val receivedInvitations: List<Invitation> = emptyList(),
        val sentInvitations: List<Invitation> = emptyList(),
        val invitationCodeText: String = "",
        val invitationCodeError: String = "",
        val familyMembers: List<UserData> = emptyList(),
        override val eventSink: (HomeEvent) -> Unit
    ) : HomeUiState, EventHandler<HomeEvent>
}

sealed interface HomeEvent : UiEvent {
    data class AcceptInvitation(
        val invitation: Invitation,
        val typedCode: String
    ) : HomeEvent

    data class DeclineInvitation(val id: String) : HomeEvent
    data class InvitationCodeChanged(val code: String) : HomeEvent
    data object NavigateToInviteFamilyMember : HomeEvent
    data object NavigateToCreateFamily : HomeEvent
    data object NavigateToSentInvitations : HomeEvent
    data object OpenAddCalendarEntry : HomeEvent
}

sealed interface HomeSideEffect : UiSideEffect {
    data object NavigateToInviteFamilyMember : HomeSideEffect
    data object NavigateToCreateFamily : HomeSideEffect
    data object NavigateToSentInvitations : HomeSideEffect
    data object OpenAddCalendarEntry : HomeSideEffect
    data class ShowError(val message: String) : HomeSideEffect
}