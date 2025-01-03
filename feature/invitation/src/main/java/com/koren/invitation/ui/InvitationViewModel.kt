
package com.koren.invitation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.data.repository.InvitationRepository
import com.koren.domain.GetFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    private val getFamilyUseCase: GetFamilyUseCase
): ViewModel() {

    private val _state = MutableStateFlow(InvitationUiState(eventSink = ::handleEvent))
    val state: StateFlow<InvitationUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val family = getFamilyUseCase()
            _state.update { currentState -> currentState.copy(familyName = family.name) }
        }
    }

    private fun handleEvent(event: InvitationEvent) {
        when (event) {
            is InvitationEvent.CreateQRInvitation -> createInvitation(_state.value)
            is InvitationEvent.CollapseCreateQRInvitation -> _state.update { currentState -> currentState.copy(isCreateQRInvitationExpanded = false) }
            is InvitationEvent.EmailInviteClick -> _state.update { currentState -> currentState.copy(isEmailInviteExpanded = !currentState.isEmailInviteExpanded) }
            is InvitationEvent.EmailInviteTextChange -> _state.update { currentState -> currentState.copy(emailInviteText = event.email) }
            is InvitationEvent.InviteViaEmailClick -> inviteViaEmail(_state.value)
        }
    }

    private fun inviteViaEmail(currentState: InvitationUiState) {
        viewModelScope.launch {
            _state.update { currentState.copy(emailInvitationLoading = true) }
            invitationRepository.createInvitationViaEmail(currentState.emailInviteText)
                .onSuccess { result ->
                    _state.update { currentState.copy(emailInvitation = result, emailInvitationLoading = false) }
                }
                .onFailure { error ->
                    _state.update { currentState.copy(errorMessage = error.message?: "Unknown error") }
                }
        }
    }

    private fun createInvitation(currentState: InvitationUiState) {
        if (currentState.qrInvitation != null) {
            _state.update { currentState.copy(isCreateQRInvitationExpanded = !currentState.isCreateQRInvitationExpanded) }
            return
        }
        viewModelScope.launch {
            _state.update { currentState.copy(qrInvitationLoading = true, isCreateQRInvitationExpanded = true) }
            invitationRepository.createInvitation()
                .onSuccess { result ->
                    _state.update { currentState.copy(qrInvitation = result, isCreateQRInvitationExpanded = true, qrInvitationLoading = false) }
                }
                .onFailure { error ->
                    _state.update { currentState.copy(errorMessage = error.message ?: "Unknown error") }
                }
        }
    }
}
