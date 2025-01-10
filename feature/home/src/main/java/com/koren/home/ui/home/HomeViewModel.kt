package com.koren.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationStatus
import com.koren.data.repository.InvitationRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    getAllFamilyMembers: GetAllFamilyMembersUseCase
): ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        combine(
            invitationRepository.getReceivedInvitations(),
            invitationRepository.getSentInvitations(),
            getAllFamilyMembers()
        ) { receivedInvitations, sentInvitations, familyMembers ->
            _state.update {
                HomeUiState.Shown(
                    receivedInvitations = receivedInvitations.filter { it.status == InvitationStatus.PENDING },
                    sentInvitations = sentInvitations,
                    familyMembers = familyMembers,
                    eventSink = ::handleEvent
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun handleEvent(event: HomeEvent) {
        withShownState { current ->
            when (event) {
                is HomeEvent.AcceptInvitation -> acceptInvitation(event.invitation, event.typedCode, current)
                is HomeEvent.DeclineInvitation -> declineInvitation(event.id)
                is HomeEvent.InvitationCodeChanged -> _state.update { current.copy(invitationCodeText = event.code, invitationCodeError = "") }
            }
        }
    }

    private fun acceptInvitation(
        invitation: Invitation,
        typedCode: String,
        current: HomeUiState.Shown
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = invitationRepository.acceptInvitation(invitation, typedCode)
            if (result.isFailure) {
                _state.update { current.copy(invitationCodeError = result.exceptionOrNull()?.message ?: "") }
            }
        }
    }

    private fun declineInvitation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.declineInvitation(id)
        }
    }

    private inline fun withShownState(action: (HomeUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is HomeUiState.Shown) {
            action(currentState)
        }
    }
}