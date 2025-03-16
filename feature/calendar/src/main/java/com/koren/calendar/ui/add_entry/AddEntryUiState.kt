package com.koren.calendar.ui.add_entry

import com.koren.common.models.calendar.Day
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.HourMinute
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface AddEntryUiState : UiState {
    data object Loading : AddEntryUiState
    sealed interface Shown : AddEntryUiState, EventHandler<AddEntryUiEvent> {
        val title: String
        val selectedDay: Day
        val showErrors: Boolean
        val titleError: Boolean
            get() = showErrors && title.isBlank()
        val hasErrors: Boolean

        data class AddEvent(
            override val title: String = "",
            override val selectedDay: Day = Day(),
            val description: String = "",
            val isAllDay: Boolean = true,
            val startDate: Long = 0L,
            val endDate: Long = 0L,
            val startTime: HourMinute? = null,
            val endTime: HourMinute? = null,
            override val showErrors: Boolean = false,
            override val eventSink: (AddEntryUiEvent) -> Unit
        ) : Shown {

            val startTimeError: Boolean
                get() = showErrors && isAllDay.not() && startTime == null

            val endTimeError: Boolean
                get() = showErrors && isAllDay.not() && endTime == null

            override val hasErrors: Boolean
                get() = titleError || startTimeError || endTimeError
        }

        data class AddTask(
            override val title: String = "",
            override val selectedDay: Day = Day(),
            val description: String = "",
            val time: HourMinute? = null,
            val selectedAssignee: UserData? = null,
            val assigneeSearchQuery: String = "",
            val assigneeDropdownExpanded: Boolean = false,
            val filteredAssignees: List<UserData> = emptyList(),
            val allFamilyMembers: List<UserData> = emptyList(),
            override val showErrors: Boolean = false,
            override val eventSink: (AddEntryUiEvent) -> Unit
        ) : Shown {

            val timeError: Boolean
                get() = showErrors && time == null

            val assigneeError: Boolean
                get() = showErrors && selectedAssignee == null

            override val hasErrors: Boolean
                get() = titleError || timeError || assigneeError
        }
    }
}

sealed interface AddEntryUiEvent : UiEvent {
    data class TitleChanged(val title: String) : AddEntryUiEvent
    data class TabChanged(val tabIndex: Int) : AddEntryUiEvent
    data class DescriptionChanged(val description: String) : AddEntryUiEvent
    data class IsAllDayChanged(val isAllDay: Boolean) : AddEntryUiEvent
    data class StartDateChanged(val startDate: Long) : AddEntryUiEvent
    data class EndDateChanged(val endDate: Long) : AddEntryUiEvent
    data class StartTimeChanged(val startTime: HourMinute) : AddEntryUiEvent
    data class EndTimeChanged(val endTime: HourMinute) : AddEntryUiEvent

    data class AssigneeSearchQueryChanged(val query: String) : AddEntryUiEvent
    data class AssigneeDropdownExpandedChanged(val expanded: Boolean) : AddEntryUiEvent
    data class AssigneeSelected(val assignee: UserData) : AddEntryUiEvent
    data object RemoveSelectedAssignee : AddEntryUiEvent

    data object SaveClicked : AddEntryUiEvent
    data object CancelClicked : AddEntryUiEvent
}

sealed interface AddEntryUiSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : AddEntryUiSideEffect
    data object Dismiss : AddEntryUiSideEffect
}