package com.koren.home.ui.home

import androidx.lifecycle.viewModelScope
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.util.StateViewModel
import com.koren.data.repository.InvitationRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    getAllFamilyMembers: GetAllFamilyMembersUseCase
): StateViewModel<HomeEvent, HomeUiState, HomeSideEffect>() {

    override fun setInitialState(): HomeUiState = HomeUiState.Loading

    init {
        combine(
            invitationRepository.getReceivedInvitations(),
            invitationRepository.getSentInvitations(),
            getAllFamilyMembers()
        ) { receivedInvitations, sentInvitations, familyMembers ->
            _uiState.update {
                HomeUiState.Shown(
                    receivedInvitations = receivedInvitations.filter { it.status == InvitationStatus.PENDING },
                    sentInvitations = sentInvitations,
                    familyMembers = familyMembers,
                    eventSink = ::handleEvent
                )
            }
        }
            .catch {
                Timber.e("Error loading home data: $it")
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: HomeEvent) {
        withEventfulState<HomeUiState.Shown> { current ->
            when (event) {
                is HomeEvent.AcceptInvitation -> acceptInvitation(event.invitation, event.typedCode, current)
                is HomeEvent.DeclineInvitation -> declineInvitation(event.id)
                is HomeEvent.InvitationCodeChanged -> _uiState.update { current.copy(invitationCodeText = event.code, invitationCodeError = "") }
                is HomeEvent.NavigateToCreateFamily -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToCreateFamily)
                is HomeEvent.NavigateToInviteFamilyMember -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToInviteFamilyMember)
                is HomeEvent.NavigateToSentInvitations -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToSentInvitations)
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
                _uiState.update { current.copy(invitationCodeError = result.exceptionOrNull()?.message ?: "") }
            }
        }
    }

    private fun declineInvitation(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            invitationRepository.declineInvitation(id)
        }
    }
}