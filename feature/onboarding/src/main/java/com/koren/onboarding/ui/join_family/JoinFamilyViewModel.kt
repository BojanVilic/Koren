package com.koren.onboarding.ui.join_family

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.util.MoleculeViewModel
import com.koren.common.util.orUnknownError
import com.koren.data.repository.InvitationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinFamilyViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository
): MoleculeViewModel<JoinFamilyUiEvent, JoinFamilyUiState, JoinFamilyUiSideEffect>() {

    override fun setInitialState(): JoinFamilyUiState = JoinFamilyUiState.Loading

    @Composable
    override fun produceState(): JoinFamilyUiState {
        val receivedInvitations by invitationRepository.getReceivedInvitations().collectAsState(initial = emptyList())

        var invitationCodeText by remember { mutableStateOf("") }
        var invitationError by remember { mutableStateOf("") }

        return JoinFamilyUiState.Shown(
            receivedInvitations = receivedInvitations.filter { it.status == InvitationStatus.PENDING },
            invitationCodeText = invitationCodeText,
            invitationCodeError = invitationError
        ) { event ->
            when (event) {
                is JoinFamilyUiEvent.AcceptInvitation -> acceptInvitation(event.invitation, event.typedCode, onError = { invitationError = it })
                is JoinFamilyUiEvent.DeclineInvitation -> declineInvitation(event.id)
                is JoinFamilyUiEvent.InvitationCodeChanged -> invitationCodeText = event.code
            }
        }
    }

    private fun acceptInvitation(
        invitation: Invitation,
        typedCode: String,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = invitationRepository.acceptInvitation(invitation, typedCode)
            if (result.isFailure) onError(result.exceptionOrNull()?.message.orUnknownError())
        }
    }

    private fun declineInvitation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.declineInvitation(id)
        }
    }
}