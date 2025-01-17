package com.koren.home.ui.qr

import androidx.lifecycle.viewModelScope
import com.koren.common.models.invitation.Invitation
import com.koren.common.util.StateViewModel
import com.koren.common.util.orUnknownError
import com.koren.data.repository.InvitationRepository
import com.koren.domain.AcceptQRInvitationUseCase
import com.koren.domain.GetQRInvitationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRInvitationViewModel @Inject constructor(
    private val acceptQRInvitationUseCase: AcceptQRInvitationUseCase,
    private val getQRInvitationUseCase: GetQRInvitationUseCase,
    private val invitationRepository: InvitationRepository
) : StateViewModel<QRInvitationUiEvent, QRInvitationUiState, QRInvitationSideEffect>() {

    override fun setInitialState(): QRInvitationUiState = QRInvitationUiState.Loading

    fun getQRInvitation(invId: String, familyId: String, invCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getQRInvitationUseCase(invId, familyId, invCode)
                .onSuccess { invitation ->
                    _uiState.update { currentState ->
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
                    _sideEffects.emitSuspended(QRInvitationSideEffect.NavigateToHomeWithError(errorMessage = error.message.orUnknownError()))
                }
        }
    }

    override fun handleEvent(event: QRInvitationUiEvent) {
        withEventfulState<QRInvitationUiState.Shown> { current ->
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
            acceptQRInvitationUseCase(invitation, qrInvCode)
                .onSuccess {
                    _sideEffects.emitSuspended(QRInvitationSideEffect.NavigateToHome)
                }
        }
    }

    private fun declineInvitation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.declineInvitation(id)
            _sideEffects.emitSuspended(QRInvitationSideEffect.NavigateToHome)
        }
    }
}