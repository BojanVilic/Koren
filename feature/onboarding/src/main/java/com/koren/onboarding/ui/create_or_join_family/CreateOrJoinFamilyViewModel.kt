package com.koren.onboarding.ui.create_or_join_family

import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateOrJoinFamilyViewModel @Inject constructor(
): StateViewModel<CreateOrJoinFamilyUiEvent, CreateOrJoinFamilyUiState, CreateOrJoinFamilyUiSideEffect>() {

    override fun setInitialState(): CreateOrJoinFamilyUiState = CreateOrJoinFamilyUiState.Loading

    init {
        _uiState.update {
            CreateOrJoinFamilyUiState.Shown(
                eventSink = { event -> handleEvent(event) }
            )
        }
    }
    override fun handleEvent(event: CreateOrJoinFamilyUiEvent) {
        withEventfulState<CreateOrJoinFamilyUiState.Shown> { currentState ->
            when (event) {
                is CreateOrJoinFamilyUiEvent.CreateFamily -> _sideEffects.emitSuspended(CreateOrJoinFamilyUiSideEffect.NavigateToOnboarding)
                is CreateOrJoinFamilyUiEvent.JoinFamily -> _sideEffects.emitSuspended(CreateOrJoinFamilyUiSideEffect.NavigateToPendingInvitationsScreen)
            }
        }
    }
}