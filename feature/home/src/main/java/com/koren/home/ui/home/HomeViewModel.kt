package com.koren.home.ui.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.koren.common.models.calendar.CalendarItem
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.data.repository.CalendarRepository
import com.koren.data.repository.InvitationRepository
import com.koren.domain.ChangeTaskStatusUseCase
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetFamilyUseCase
import com.koren.domain.GetNextCalendarItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase
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
            val upcomingItem by getNextCalendarItemUseCase().collectAsState(initial = CalendarItem.None)


            when (val currentState = uiState.collectAsState().value) {
                is HomeUiState.Loading -> {
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
                is HomeUiState.Shown -> {
                    _uiState.update {
                        currentState.copy(
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
            }
        }
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
                is HomeEvent.TaskCompletionButtonClicked -> changeTaskStatus(event.task.taskId, !event.task.completed)
                is HomeEvent.FamilyMemberClicked -> {
//                    _uiState.update { current.copy(bottomSheetContent = HomeBottomSheetContent.MemberDetails(event.member)) }
                    _sideEffects.emitSuspended(HomeSideEffect.OpenMemberDetails(event.member))
                }
                is HomeEvent.DismissBottomSheet -> {
                    _sideEffects.emitSuspended(HomeSideEffect.DismissBottomSheet)
                    _uiState.update { current.copy(bottomSheetContent = HomeBottomSheetContent.None) }
                }
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

    private fun changeTaskStatus(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            changeTaskStatusUseCase(taskId, isCompleted)
        }
    }
}