package com.koren.activity.ui

import com.koren.common.models.activity.UserLocationActivity
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ActivityUiState : UiState {
    data object Loading : ActivityUiState
    data object Error : ActivityUiState
    data object NoFamily : ActivityUiState
    data class Shown(
        val activities: List<UserLocationActivity>,
        val fetchingMore: Boolean = false,
        val canFetchMore: Boolean = true,
        override val eventSink: (ActivityEvent) -> Unit
    ): ActivityUiState, EventHandler<ActivityEvent>
}

sealed interface ActivityEvent : UiEvent {
    data object NavigateToCalendar : ActivityEvent
    data object FetchMoreActivities : ActivityEvent
    data object AnswersClicked : ActivityEvent
}

sealed interface ActivitySideEffect : UiSideEffect {
    data object NavigateToCalendar : ActivitySideEffect
    data object NavigateToAnswers : ActivitySideEffect
}