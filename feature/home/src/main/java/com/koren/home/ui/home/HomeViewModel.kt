package com.koren.home.ui.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.koren.common.models.calendar.CalendarItem
import com.koren.common.models.family.CallHomeRequestStatus
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.data.repository.CalendarRepository
import com.koren.data.repository.InvitationRepository
import com.koren.domain.ChangeTaskStatusUseCase
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetCallHomeRequestUseCase
import com.koren.domain.GetDistanceToHomeUseCase
import com.koren.domain.GetFamilyUseCase
import com.koren.domain.GetNextCalendarItemUseCase
import com.koren.domain.UpdateCallHomeStatusUseCase
import com.koren.home.ui.home.ui_models.FamilyMemberUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    getAllFamilyMembers: GetAllFamilyMembersUseCase,
    getFamilyUseCase: GetFamilyUseCase,
    calendarRepository: CalendarRepository,
    userSession: UserSession,
    getNextCalendarItemUseCase: GetNextCalendarItemUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val getCallHomeRequestUseCase: GetCallHomeRequestUseCase,
    private val updateCallHomeStatusUseCase: UpdateCallHomeStatusUseCase,
    private val getDistanceToHomeUseCase: GetDistanceToHomeUseCase
): StateViewModel<HomeEvent, HomeUiState, HomeSideEffect>() {

    override fun setInitialState(): HomeUiState = HomeUiState.Loading

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    init {
        scope.launchMolecule(mode = RecompositionMode.ContextClock) {
            val currentUser by userSession.currentUser.collectAsState(initial = UserData())
            val events by calendarRepository.getEventsForDay(LocalDate.now(ZoneOffset.UTC)).collectAsState(initial = emptyList())
            val tasks by calendarRepository.getTasksForDayAndUser(LocalDate.now(ZoneOffset.UTC)).collectAsState(initial = emptyList())
            val receivedInvitations by invitationRepository.getReceivedInvitations().collectAsState(initial = emptyList())
            val sentInvitations by invitationRepository.getSentInvitations().collectAsState(initial = emptyList())
            val familyMembers by getAllFamilyMembers().collectAsState(initial = emptyList())
            val family by getFamilyUseCase.getFamilyFlow().collectAsState(initial = null)
            val glanceItem by getNextCalendarItemUseCase().collectAsState(initial = CalendarItem.None)
            val callHomeRequest by getCallHomeRequestUseCase().collectAsState(initial = null)

            val familyMemberUserData = familyMembers
                .map {
                    val isGoingHome = family?.callHomeRequests?.get(it.id)?.status == CallHomeRequestStatus.ACCEPTED
                    FamilyMemberUserData(
                        userData = it,
                        distance = if (isGoingHome) getDistanceToHomeUseCase(it.id).collectAsState(initial = 0).value else 0,
                        goingHome = isGoingHome
                    )
                }

            when (val currentState = uiState.collectAsState().value) {
                is HomeUiState.Loading -> {
                    _uiState.update {
                        HomeUiState.Shown(
                            currentUser = currentUser,
                            receivedInvitations = receivedInvitations.filter { it.status == InvitationStatus.PENDING },
                            sentInvitations = sentInvitations,
                            familyMembers = familyMemberUserData,
                            family = family,
                            events = events,
                            tasks = tasks,
                            freeDayNextItem = glanceItem.toNextItem(),
                            callHomeRequest = callHomeRequest,
                            eventSink = ::handleEvent
                        )
                    }
                }
                is HomeUiState.Shown -> {
                    _uiState.update {
                        currentState.copy(
                            currentUser = currentUser,
                            receivedInvitations = receivedInvitations.filter { it.status == InvitationStatus.PENDING },
                            sentInvitations = sentInvitations,
                            familyMembers = familyMemberUserData,
                            family = family,
                            events = events,
                            tasks = tasks,
                            freeDayNextItem = glanceItem.toNextItem(),
                            callHomeRequest = callHomeRequest,
                            eventSink = ::handleEvent
                        )
                    }
                }
            }
        }
    }

    override fun handleEvent(event: HomeEvent) {
        withEventfulState<HomeUiState.Shown> { current ->
            when (event) {
                is HomeEvent.AcceptInvitation -> acceptInvitation(event.invitation, event.typedCode, current)
                is HomeEvent.DeclineInvitation -> declineInvitation(event.id)
                is HomeEvent.InvitationCodeChanged -> _uiState.update { current.copy(invitationCodeText = event.code, invitationCodeError = "") }
                is HomeEvent.NavigateToInviteFamilyMember -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToInviteFamilyMember)
                is HomeEvent.NavigateToSentInvitations -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToSentInvitations)
                is HomeEvent.OpenAddCalendarEntry -> _sideEffects.emitSuspended(HomeSideEffect.OpenAddCalendarEntry)
                is HomeEvent.TaskCompletionButtonClicked -> changeTaskStatus(event.taskId, event.completed)
                is HomeEvent.FamilyMemberClicked -> _sideEffects.emitSuspended(HomeSideEffect.OpenMemberDetails(event.member.userData))
                is HomeEvent.AcceptCallHomeRequest -> updateCallHomeRequestStatus(CallHomeRequestStatus.ACCEPTED)
                is HomeEvent.RejectCallHomeRequest -> updateCallHomeRequestStatus(CallHomeRequestStatus.REJECTED)
                is HomeEvent.ActionsFabClicked -> _uiState.update { current.copy(actionsOpen = !current.actionsOpen) }
            }
        }
    }

    private fun updateCallHomeRequestStatus(status: CallHomeRequestStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            updateCallHomeStatusUseCase(status)
                .onSuccess { _sideEffects.emitSuspended(HomeSideEffect.ShowMessage(it)) }
                .onFailure { _sideEffects.emitSuspended(HomeSideEffect.ShowMessage(it.message ?: "Failed to update call home request.")) }
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

    private fun changeTaskStatus(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            changeTaskStatusUseCase(taskId, isCompleted)
        }
    }
}