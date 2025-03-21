package com.koren.home.ui.home

import androidx.lifecycle.viewModelScope
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.data.repository.CalendarRepository
import com.koren.data.repository.InvitationRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetFamilyUseCase
import com.koren.domain.GetNextCalendarItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    getAllFamilyMembers: GetAllFamilyMembersUseCase,
    getFamilyUseCase: GetFamilyUseCase,
    calendarRepository: CalendarRepository,
    userSession: UserSession,
    getNextCalendarItemUseCase: GetNextCalendarItemUseCase
): StateViewModel<HomeEvent, HomeUiState, HomeSideEffect>() {

    override fun setInitialState(): HomeUiState = HomeUiState.Loading

    private val calendarFlows = combine(
        userSession.currentUser,
        calendarRepository.getEventsForDay(LocalDate.now()),
        calendarRepository.getTasksForDay(LocalDate.now())
    ) { currentUser, events, tasks -> Triple(currentUser, events, tasks) }

    private val invitationFlows = combine(
        invitationRepository.getReceivedInvitations(),
        invitationRepository.getSentInvitations()
    ) { receivedInvitations, sentInvitations -> Pair(receivedInvitations, sentInvitations) }

    private val familyFlows = combine(
        getAllFamilyMembers(),
        getFamilyUseCase.getFamilyFlow()
    ) { familyMembers, family -> Pair(familyMembers, family) }

    init {
        combine(
            invitationFlows,
            familyFlows,
            calendarFlows,
            getNextCalendarItemUseCase()
        ) { (receivedInvitations, sentInvitations), (familyMembers, family), (currentUser, events, tasks), upcomingItem ->
            _uiState.update {
                HomeUiState.Shown(
                    currentUser = currentUser,
                    receivedInvitations = receivedInvitations.filter { it.status == InvitationStatus.PENDING },
                    sentInvitations = sentInvitations,
                    familyMembers = familyMembers,
                    family = family,
                    events = events,
                    tasks = tasks,
                    freeDayNextItem = upcomingItem.toNextItem(),
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
                is HomeEvent.OpenAddCalendarEntry -> _sideEffects.emitSuspended(HomeSideEffect.OpenAddCalendarEntry)
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