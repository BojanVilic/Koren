package com.koren.onboarding.ui.create_or_join_family

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface CreateOrJoinFamilyUiState : UiState {
    data object Loading : CreateOrJoinFamilyUiState
    data class Shown(
        override val eventSink: (CreateOrJoinFamilyUiEvent) -> Unit
    ) : CreateOrJoinFamilyUiState, EventHandler<CreateOrJoinFamilyUiEvent>
}

sealed interface CreateOrJoinFamilyUiEvent : UiEvent {
    data object CreateFamily : CreateOrJoinFamilyUiEvent
    data object JoinFamily : CreateOrJoinFamilyUiEvent
}

sealed interface CreateOrJoinFamilyUiSideEffect : UiSideEffect {
    data object NavigateToPendingInvitationsScreen : CreateOrJoinFamilyUiSideEffect
    data object NavigateToOnboarding : CreateOrJoinFamilyUiSideEffect
}