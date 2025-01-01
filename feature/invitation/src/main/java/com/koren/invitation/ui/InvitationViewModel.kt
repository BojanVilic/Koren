package com.koren.invitation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.data.repository.InvitationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository
): ViewModel() {

    private val _state = MutableStateFlow<InvitationUiState>(InvitationUiState.Idle(eventSink = ::handleEvent))
    val state: StateFlow<InvitationUiState> = _state.asStateFlow()

    private fun handleEvent(event: InvitationEvent) {
        withIdleStep { currentState ->
            when (event) {
                is InvitationEvent.CreateQRInvitation -> createInvitation(currentState)
                is InvitationEvent.CollapseCreateQRInvitation -> _state.update { currentState.copy(isCreateQRInvitationExpanded = false) }
                is InvitationEvent.EmailInviteClick -> _state.update { currentState.copy(isEmailInviteExpanded = !currentState.isEmailInviteExpanded) }
                is InvitationEvent.EmailInviteTextChange -> _state.update { currentState.copy(emailInviteText = event.email) }
                is InvitationEvent.InviteViaEmailClick -> inviteViaEmail(currentState)
            }
        }
    }

    private fun inviteViaEmail(currentState: InvitationUiState.Idle) {
        viewModelScope.launch {
            _state.update { currentState.copy(loading = true) }
//            invitationRepository.inviteViaEmail(currentState.emailInviteText)
//                .onSuccess { result ->
//                    _state.update { InvitationUiState.EmailInvitationCreated(result) }
//                }
//                .onFailure {
//                    _state.update { InvitationUiState.Error }
//                }
        }
    }

    private inline fun withIdleStep(action: (InvitationUiState.Idle) -> Unit) {
        val currentState = _state.value
        if (currentState is InvitationUiState.Idle) {
            action(currentState)
        }
    }

    private fun createInvitation(currentState: InvitationUiState.Idle) {
        if (currentState.qrInvitation != null) {
            _state.update { currentState.copy(isCreateQRInvitationExpanded = !currentState.isCreateQRInvitationExpanded) }
            return
        }
        viewModelScope.launch {
            _state.update { currentState.copy(loading = true, isCreateQRInvitationExpanded = true) }
            invitationRepository.createInvitation()
                .onSuccess { result ->
                    _state.update { currentState.copy(qrInvitation = result, isCreateQRInvitationExpanded = true, loading = false) }
                }
                .onFailure {
                    _state.update { InvitationUiState.Error }
                }
        }
    }
}