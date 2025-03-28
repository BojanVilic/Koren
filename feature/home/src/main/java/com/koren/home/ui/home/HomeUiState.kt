package com.koren.home.ui.home

import com.koren.common.models.calendar.CalendarItem
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.EventWithUsers
import com.koren.common.models.calendar.Task
import com.koren.common.models.calendar.TaskWithUsers
import com.koren.common.models.family.Family
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface NextItem {
    data class TaskItem(val task: TaskWithUsers) : NextItem
    data class EventItem(val event: EventWithUsers) : NextItem
    data object None : NextItem
}

sealed interface HomeUiState : UiState {
    data object Loading : HomeUiState
    data class Shown(
        val currentUser: UserData = UserData(),
        val receivedInvitations: List<Invitation> = emptyList(),
        val sentInvitations: List<Invitation> = emptyList(),
        val invitationCodeText: String = "",
        val invitationCodeError: String = "",
        val familyMembers: List<UserData> = emptyList(),
        val family: Family? = null,
        val tasks: List<Task> = emptyList(),
        val events: List<Event> = emptyList(),
        val freeDayNextItem: NextItem = NextItem.None,
        override val eventSink: (HomeEvent) -> Unit
    ) : HomeUiState, EventHandler<HomeEvent>
}

sealed interface HomeEvent : UiEvent {
    data class AcceptInvitation(
        val invitation: Invitation,
        val typedCode: String
    ) : HomeEvent

    data class DeclineInvitation(val id: String) : HomeEvent
    data class InvitationCodeChanged(val code: String) : HomeEvent
    data object NavigateToInviteFamilyMember : HomeEvent
    data object NavigateToCreateFamily : HomeEvent
    data object NavigateToSentInvitations : HomeEvent
    data object OpenAddCalendarEntry : HomeEvent
    data class TaskCompletionButtonClicked(val task: Task) : HomeEvent
    data class FamilyMemberClicked(val member: UserData) : HomeEvent
}

sealed interface HomeSideEffect : UiSideEffect {
    data object NavigateToInviteFamilyMember : HomeSideEffect
    data object NavigateToCreateFamily : HomeSideEffect
    data object NavigateToSentInvitations : HomeSideEffect
    data object OpenAddCalendarEntry : HomeSideEffect
    data class OpenMemberDetails(val member: UserData) : HomeSideEffect
    data class ShowError(val message: String) : HomeSideEffect
}

fun CalendarItem.toNextItem(): NextItem {
    return when (this) {
        is CalendarItem.EventItem -> NextItem.EventItem(event)
        is CalendarItem.TaskItem -> NextItem.TaskItem(task)
        CalendarItem.None -> NextItem.None
    }
}