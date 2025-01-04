package com.koren.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationStatus
import com.koren.data.repository.InvitationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository
): ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.getReceivedInvitations().collect { invitations ->
                val pendingInvitations = invitations.filter { it.status == InvitationStatus.PENDING }
                _state.update { currentState ->
                    if (currentState is HomeUiState.Shown)
                        currentState.copy(receivedInvitations = pendingInvitations)
                    else
                        HomeUiState.Shown(
                            receivedInvitations = pendingInvitations,
                            eventSink = ::handleEvent
                        )
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.getSentInvitations().collect { invitations ->
                _state.update { currentState ->
                    if (currentState is HomeUiState.Shown)
                        currentState.copy(sentInvitations = invitations)
                    else
                        HomeUiState.Shown(
                            sentInvitations = invitations,
                            eventSink = ::handleEvent
                        )
                }
            }
        }
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