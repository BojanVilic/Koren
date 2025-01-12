package com.koren.home.ui.sent_invitations

import androidx.lifecycle.viewModelScope
import com.koren.common.util.StateViewModel
import com.koren.data.repository.InvitationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SentInvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository
) : StateViewModel<SentInvitationEvent, SentInvitationUiState, Nothing>() {

    override fun setInitialState(): SentInvitationUiState = SentInvitationUiState.Loading

    init {
        viewModelScope.launch(Dispatchers.Default) {
            invitationRepository.getSentInvitations().collect { sentInvitations ->
                val uiSentInvitations = sentInvitations.map { UiSentInvitation(invitation = it) }
                if (uiSentInvitations.isEmpty())
                    _uiState.update { SentInvitationUiState.Empty }
                else _uiState.update {
                    SentInvitationUiState.Shown(
                        sentInvitations = uiSentInvitations,
                        eventSink = ::handleEvent
                    )
                }
            }
        }
    }

    override fun handleEvent(event: SentInvitationEvent) {
        withEventfulState<SentInvitationUiState.Shown> { current ->
            when (event) {
                is SentInvitationEvent.ExpandQRCode -> expandOrCollapseQRInvitation(event.id, current)
            }
        }
    }

    private fun expandOrCollapseQRInvitation(id: String, current: SentInvitationUiState.Shown) {
        val updatedInvitations = current.sentInvitations.map { uiInvitation ->
            if (uiInvitation.invitation.id == id) uiInvitation.copy(expanded = !uiInvitation.expanded)
            else uiInvitation
        }
        _uiState.update { current.copy(sentInvitations = updatedInvitations) }
    }
}