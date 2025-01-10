package com.koren.home.ui.sent_invitations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.data.repository.InvitationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SentInvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SentInvitationUiState>(SentInvitationUiState.Loading)
    val state: StateFlow<SentInvitationUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            invitationRepository.getSentInvitations().collect { sentInvitations ->
                val uiSentInvitations = sentInvitations.map { UiSentInvitation(invitation = it) }
                _state.update {
                    SentInvitationUiState.Shown(
                        sentInvitations = uiSentInvitations,
                        eventSink = ::handleEvent
                    )
                }
            }
        }
    }

    private fun handleEvent(event: SentInvitationEvent) {
        withShownState { current ->
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
        _state.update { current.copy(sentInvitations = updatedInvitations) }
    }

    private inline fun withShownState(action: (SentInvitationUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is SentInvitationUiState.Shown) {
            action(currentState)
        }
    }
}