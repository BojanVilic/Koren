package com.carfax.manage_familiy.select_member

import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface SelectMemberUiState : UiState {
    data object Loading : SelectMemberUiState
    data class Shown(
        val members: List<UserData> = emptyList(),
        override val eventSink: (SelectMemberUiEvent) -> Unit
    ) : SelectMemberUiState, EventHandler<SelectMemberUiEvent>
}

sealed interface SelectMemberUiEvent : UiEvent {
    data class MemberSelected(val member: UserData) : SelectMemberUiEvent
    data object AddMemberClicked : SelectMemberUiEvent
}

sealed interface SelectMemberUiSideEffect : UiSideEffect {
    data class NavigateToEditMember(val memberId: String) : SelectMemberUiSideEffect
    data object NavigateToAddNewMember : SelectMemberUiSideEffect
}