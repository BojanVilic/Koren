
package com.koren.invitation.ui

import androidx.lifecycle.viewModelScope
import com.koren.common.util.StateViewModel
import com.koren.data.repository.InvitationRepository
import com.koren.domain.GetFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    private val getFamilyUseCase: GetFamilyUseCase
): StateViewModel<InvitationEvent, InvitationUiState, Nothing>() {

    override fun setInitialState(): InvitationUiState = InvitationUiState.Shown(eventSink = ::handleEvent)

    init {
        viewModelScope.launch {
            getFamilyUseCase()
                .onSuccess { family -> withEventfulState<InvitationUiState.Shown> { currentState -> _uiState.update { currentState.copy(familyName = family.name) } } }
                .onFailure { error -> _uiState.update { InvitationUiState.Error(errorMessage = error.message ?: "Unknown error") } }
        }
    }

    override fun handleEvent(event: InvitationEvent) {
        withEventfulState<InvitationUiState.Shown> { currentState ->
            when (event) {
                is InvitationEvent.CreateQRInvitation -> createInvitation(currentState)
                is InvitationEvent.CollapseCreateQRInvitation -> _uiState.update { currentState.copy(isCreateQRInvitationExpanded = false) }
                is InvitationEvent.EmailInviteClick -> _uiState.update { currentState.copy(isEmailInviteExpanded = !currentState.isEmailInviteExpanded) }
                is InvitationEvent.EmailInviteTextChange -> _uiState.update { currentState.copy(emailInviteText = event.email) }
                is InvitationEvent.InviteViaEmailClick -> inviteViaEmail(currentState)
            }
        }
    }

    private fun inviteViaEmail(currentState: InvitationUiState.Shown) {
        viewModelScope.launch {
            _uiState.update { currentState.copy(emailInvitationLoading = true) }
            invitationRepository.createInvitationViaEmail(currentState.emailInviteText)
                .onSuccess { result ->
                    _uiState.update { currentState.copy(emailInvitation = result, emailInvitationLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { currentState.copy(errorMessage = error.message?: "Unknown error") }
                }
        }
    }

    private fun createInvitation(currentState: InvitationUiState.Shown) {
        if (currentState.qrInvitation != null) {
            _uiState.update { currentState.copy(isCreateQRInvitationExpanded = !currentState.isCreateQRInvitationExpanded) }
            return
        }
        viewModelScope.launch {
            _uiState.update { currentState.copy(qrInvitationLoading = true, isCreateQRInvitationExpanded = true) }
            invitationRepository.createInvitation()
                .onSuccess { result ->
                    _uiState.update { currentState.copy(qrInvitation = result, isCreateQRInvitationExpanded = true, qrInvitationLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { currentState.copy(errorMessage = error.message ?: "Unknown error") }
                }
        }
    }
}
