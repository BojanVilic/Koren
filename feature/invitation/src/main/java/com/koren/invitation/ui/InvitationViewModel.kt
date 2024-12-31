package com.koren.invitation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koren.data.repository.InvitationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
        withIdleStep {
            when (event) {
                is InvitationEvent.CreateInvitation -> createInvitation()
            }
        }
    }

    private inline fun withIdleStep(action: (InvitationUiState.Idle) -> Unit) {
        val currentState = _state.value
        if (currentState is InvitationUiState.Idle) {
            action(currentState)
        }
    }

    private fun createInvitation() {
        viewModelScope.launch {
            _state.value = InvitationUiState.Loading
            invitationRepository.createInvitation()
                .onSuccess { result ->
                    _state.update { InvitationUiState.InvitationCreated(result) }
                }
                .onFailure {
                    _state.update { InvitationUiState.Error }
                }
        }
    }
}