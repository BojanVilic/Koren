package com.koren.home.ui.home.member_details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.koren.common.models.family.CallHomeRequestStatus
import com.koren.common.models.family.Family
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.common.util.formatDistanceToText
import com.koren.common.util.orUnknownError
import com.koren.designsystem.icon.CallHome
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.MapSelected
import com.koren.designsystem.icon.Task
import com.koren.domain.ExistingRequestException
import com.koren.domain.GetAssignedTasksForUserUseCase
import com.koren.domain.GetDistanceBetweenTwoUsersUseCase
import com.koren.domain.GetFamilyMemberUseCase
import com.koren.domain.GetFamilyUseCase
import com.koren.domain.SendCallHomeRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MemberDetailsViewModel @Inject constructor(
    private val getFamilyMemberUseCase: GetFamilyMemberUseCase,
    private val userSession: UserSession,
    private val sendCallHomeRequestUseCase: SendCallHomeRequestUseCase,
    private val getFamilyUseCase: GetFamilyUseCase,
    private val getAssignedTasksForUserUseCase: GetAssignedTasksForUserUseCase,
    private val getDistanceBetweenUsers: GetDistanceBetweenTwoUsersUseCase
): StateViewModel<MemberDetailsUiEvent, MemberDetailsUiState, MemberDetailsUiSideEffect>() {

    override fun setInitialState(): MemberDetailsUiState = MemberDetailsUiState.Loading

    private var taskCollectionJob: Job? = null

    fun init(userId: String) {
        viewModelScope.launch {
            combine(
                userSession.currentUser,
                getFamilyMemberUseCase(userId),
                getFamilyUseCase.getFamilyFlow()
            ) { currentUser, familyMemberDetails, family ->
                Triple(currentUser, familyMemberDetails, family)
            }
            .collect { (currentUser, familyMemberDetails, family) ->
                if (currentUser.id == userId) {
                    _uiState.update {
                        MemberDetailsUiState.SelfDetails
                    }
                    return@collect
                }
                val distanceString = getDistanceBetweenUsers(currentUser, familyMemberDetails).formatDistanceToText()

                _uiState.getAndUpdate {
                    when (it) {
                        is MemberDetailsUiState.Shown -> it.copy(
                            member = familyMemberDetails,
                            distanceText = distanceString,
                            options = getOptions(family, familyMemberDetails),
                            eventSink = { event -> handleEvent(event) }
                        )
                        else -> MemberDetailsUiState.Shown(
                            member = familyMemberDetails,
                            distanceText = distanceString,
                            options = getOptions(family, familyMemberDetails),
                            eventSink = { event -> handleEvent(event) }
                        )
                    }
                }
            }
        }
    }

    private fun getOptions(
        family: Family?,
        familyMemberDetails: UserData
    ) = listOf(
        MemberDetailsOption(
            icon = KorenIcons.CallHome,
            title = "Call home",
            event = MemberDetailsUiEvent.CallHome,
            isEnabled = family?.callHomeRequests?.containsKey(familyMemberDetails.id) == false,
            description = if (family?.callHomeRequests?.get(familyMemberDetails.id)?.status == CallHomeRequestStatus.REQUESTED) {
                "Request sent. Waiting for response."
            } else {
                ""
            }
        )
    ) + staticOptions

    override fun handleEvent(event: MemberDetailsUiEvent) {
        withEventfulState<MemberDetailsUiState.Shown> { currentState ->
            when (event) {
                is MemberDetailsUiEvent.CallHome -> sendCallHomeRequest(currentState.member.id)
                is MemberDetailsUiEvent.FindOnMap -> _sideEffects.emitSuspended(MemberDetailsUiSideEffect.NavigateAndFindOnMap(currentState.member.id))
                is MemberDetailsUiEvent.ViewAssignedTasks -> loadAssignedTasksForUser(currentState.copy(showViewAssignedTasksDialog = true))
                is MemberDetailsUiEvent.EditRole -> Unit
                is MemberDetailsUiEvent.DismissTasksDialog -> _uiState.update { currentState.copy(showViewAssignedTasksDialog = false) }
                is MemberDetailsUiEvent.SelectTimeRange -> {
                    val newState = _uiState.updateAndGet { currentState.copy(selectedTimeRange = event.timeRange) }
                    loadAssignedTasksForUser(newState as MemberDetailsUiState.Shown)
                }
            }
        }
    }

    private fun loadAssignedTasksForUser(
        currentState: MemberDetailsUiState.Shown
    ) {
        taskCollectionJob?.cancel()
        taskCollectionJob = viewModelScope.launch {
            getAssignedTasksForUserUseCase(currentState.member.id, currentState.selectedTimeRange)
                .collect { tasks -> _uiState.update { currentState.copy(assignedTasks = tasks) } }
        }
    }

    private fun sendCallHomeRequest(targetUserId: String) {
        viewModelScope.launch {
            sendCallHomeRequestUseCase(targetUserId)
                .onSuccess { _sideEffects.emitSuspended(MemberDetailsUiSideEffect.ShowSnackbarMessage("Call home request sent successfully.")) }
                .onFailure { error ->
                    when (error) {
                        is ExistingRequestException -> _sideEffects.emitSuspended(MemberDetailsUiSideEffect.ShowSnackbarMessage(error.message.orUnknownError()))
                        else -> _sideEffects.emitSuspended(MemberDetailsUiSideEffect.ShowSnackbarMessage("Failed to send call home request."))
                    }
                }
        }
    }

    private val staticOptions = listOf(
        MemberDetailsOption(
            icon = KorenIcons.MapSelected,
            title = "Find on map",
            event = MemberDetailsUiEvent.FindOnMap
        ),
        MemberDetailsOption(
            icon = KorenIcons.Task,
            title = "View assigned tasks",
            event = MemberDetailsUiEvent.ViewAssignedTasks
        )
    )

    override fun onCleared() {
        super.onCleared()
        taskCollectionJob?.cancel()
    }
}