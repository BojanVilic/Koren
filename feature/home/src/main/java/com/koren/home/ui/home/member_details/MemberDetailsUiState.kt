package com.koren.home.ui.home.member_details

import com.koren.common.models.calendar.Task
import com.koren.common.models.calendar.TaskTimeRange
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface MemberDetailsUiState : UiState {
    data object Loading : MemberDetailsUiState
    data object SelfDetails : MemberDetailsUiState
    data class Shown(
        val member: UserData = UserData(),
        val distanceText: String = "",
        val options: List<MemberDetailsOption> = emptyList(),
        val assignedTasks: List<Task> = emptyList(),
        val showViewAssignedTasksDialog: Boolean = false,
        val selectedTimeRange: TaskTimeRange = TaskTimeRange.Next7Days,
        override val eventSink: (MemberDetailsUiEvent) -> Unit
    ) : MemberDetailsUiState, EventHandler<MemberDetailsUiEvent>
}

sealed interface MemberDetailsUiEvent : UiEvent {
    data object EditRole : MemberDetailsUiEvent
    data object CallHome : MemberDetailsUiEvent
    data object FindOnMap : MemberDetailsUiEvent
    data object ViewAssignedTasks : MemberDetailsUiEvent
    data object DismissTasksDialog : MemberDetailsUiEvent
    data class SelectTimeRange(val timeRange: TaskTimeRange) : MemberDetailsUiEvent
}

sealed interface MemberDetailsUiSideEffect : UiSideEffect {
    data class NavigateAndFindOnMap(val userId: String) : MemberDetailsUiSideEffect
    data class ShowSnackbarMessage(val message: String) : MemberDetailsUiSideEffect
}