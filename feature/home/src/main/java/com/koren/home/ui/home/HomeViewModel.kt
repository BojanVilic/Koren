package com.koren.home.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.koren.common.models.calendar.CalendarItem
import com.koren.common.models.family.CallHomeRequestStatus
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.common.util.orUnknownError
import com.koren.data.repository.CalendarRepository
import com.koren.data.repository.InvitationRepository
import com.koren.domain.ChangeTaskStatusUseCase
import com.koren.domain.GetAllFamilyMembersWithDetailsUseCase
import com.koren.domain.GetCallHomeRequestUseCase
import com.koren.domain.GetNextCalendarItemUseCase
import com.koren.domain.UpdateBatteryLevelUseCase
import com.koren.domain.UpdateCallHomeStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    private val calendarRepository: CalendarRepository,
    private val userSession: UserSession,
    private val getNextCalendarItemUseCase: GetNextCalendarItemUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val getCallHomeRequestUseCase: GetCallHomeRequestUseCase,
    private val updateCallHomeStatusUseCase: UpdateCallHomeStatusUseCase,
    private val getAllFamilyMembersWithDetailsUseCase: GetAllFamilyMembersWithDetailsUseCase,
    private val updateBatteryLevelUseCase: UpdateBatteryLevelUseCase
): MoleculeViewModel<HomeEvent, HomeUiState, HomeSideEffect>() {

    override fun setInitialState(): HomeUiState = HomeUiState.Loading

    @Composable
    override fun produceState(): HomeUiState {
        val currentUser by userSession.currentUser.collectAsState(initial = UserData())
        val events by calendarRepository.getEventsForDay(LocalDate.now(ZoneOffset.UTC)).collectAsState(initial = emptyList())
        val tasks by calendarRepository.getTasksForDayAndUser(LocalDate.now(ZoneOffset.UTC)).collectAsState(initial = emptyList())
        val receivedInvitations by invitationRepository.getReceivedInvitations().collectAsState(initial = emptyList())
        val sentInvitations by invitationRepository.getSentInvitations().collectAsState(initial = emptyList())
        val glanceItem by getNextCalendarItemUseCase().collectAsState(initial = CalendarItem.None)
        val callHomeRequest by getCallHomeRequestUseCase().collectAsState(initial = null)
        val familyMemberUserData by getAllFamilyMembersWithDetailsUseCase().collectAsState(initial = emptyList())

        var invitationCodeText by remember { mutableStateOf("") }
        var invitationError by remember { mutableStateOf("") }
        var actionsOpen by remember { mutableStateOf(false) }

        if (currentUser.id.isBlank()) return HomeUiState.Loading

        LaunchedEffect(Unit) {
            updateBatteryLevelUseCase()
        }

        return HomeUiState.Shown(
            currentUser = currentUser,
            receivedInvitations = receivedInvitations.filter { it.status == InvitationStatus.PENDING },
            invitationCodeText = invitationCodeText,
            actionsOpen = actionsOpen,
            sentInvitations = sentInvitations,
            familyMembers = familyMemberUserData,
            events = events,
            tasks = tasks,
            freeDayNextItem = glanceItem.toNextItem(),
            callHomeRequest = callHomeRequest
        ) { event ->
            when (event) {
                is HomeEvent.AcceptInvitation -> acceptInvitation(event.invitation, event.typedCode, onError = { invitationError = it })
                is HomeEvent.DeclineInvitation -> declineInvitation(event.id)
                is HomeEvent.InvitationCodeChanged -> invitationCodeText = event.code
                is HomeEvent.NavigateToInviteFamilyMember -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToInviteFamilyMember)
                is HomeEvent.NavigateToSentInvitations -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToSentInvitations)
                is HomeEvent.OpenAddCalendarEntry -> _sideEffects.emitSuspended(HomeSideEffect.OpenAddCalendarEntry)
                is HomeEvent.TaskCompletionButtonClicked -> changeTaskStatus(event.taskId, event.completed)
                is HomeEvent.FamilyMemberClicked -> _sideEffects.emitSuspended(HomeSideEffect.OpenMemberDetails(event.member.userData))
                is HomeEvent.AcceptCallHomeRequest -> updateCallHomeRequestStatus(CallHomeRequestStatus.ACCEPTED)
                is HomeEvent.RejectCallHomeRequest -> updateCallHomeRequestStatus(CallHomeRequestStatus.REJECTED)
                is HomeEvent.ActionsFabClicked -> actionsOpen = !actionsOpen
                is HomeEvent.NavigateToChat -> _sideEffects.emitSuspended(HomeSideEffect.NavigateToChat)
                is HomeEvent.RequestPickUpClicked -> Unit
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

    private fun changeTaskStatus(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            changeTaskStatusUseCase(taskId, isCompleted)
        }
    }
}