package com.koren.home.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.common.models.Invitation
import com.koren.data.repository.InvitationRepository
import com.koren.domain.AcceptQRInvitationUseCase
import com.koren.domain.GetQRInvitationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRInvitationViewModel @Inject constructor(
    private val acceptQRInvitationUseCase: AcceptQRInvitationUseCase,
    private val getQRInvitationUseCase: GetQRInvitationUseCase,
    private val invitationRepository: InvitationRepository
) : ViewModel() {

    private val _state = MutableStateFlow<QRInvitationUiState>(QRInvitationUiState.Loading)
    val state: StateFlow<QRInvitationUiState> = _state.asStateFlow()

    fun getQRInvitation(invId: String, familyId: String, invCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getQRInvitationUseCase(invId, familyId, invCode)
                .onSuccess { invitation ->
                    _state.update { currentState ->
                        if (currentState is QRInvitationUiState.Shown)
                            currentState.copy(invitation = invitation)
                        else
                            QRInvitationUiState.Shown(
                                invitation = invitation,
                                eventSink = ::handleEvent
                            )
                    }
                }
                .onFailure { error ->
                    _state.update { QRInvitationUiState.NavigateToHome(errorMessage = error.message?: "Unknown error.")
                }
            }
        }
    }

    private fun handleEvent(event: QRInvitationUiEvent) {
        withShownState { current ->
            when (event) {
                is QRInvitationUiEvent.AcceptInvitation -> acceptInvitation(current.invitation, event.qrInvCode)
                is QRInvitationUiEvent.DeclineInvitation -> declineInvitation(current.invitation.id)
            }
        }
    }

    private fun acceptInvitation(
        invitation: Invitation,
        qrInvCode: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            acceptQRInvitationUseCase(invitation, qrInvCode).onSuccess {
                _state.update { QRInvitationUiState.NavigateToHome() }
            }
        }
    }

    private fun declineInvitation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.declineInvitation(id)
            _state.update { QRInvitationUiState.NavigateToHome() }
        }
    }

    private inline fun withShownState(action: (QRInvitationUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is QRInvitationUiState.Shown) {
            action(currentState)
        }
    }
}