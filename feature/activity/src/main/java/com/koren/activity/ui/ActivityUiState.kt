package com.koren.activity.ui

import com.koren.common.models.activity.LocationActivity
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ActivityUiState : UiState {
    data object Loading : ActivityUiState
    data object Error : ActivityUiState
    data object NoFamily : ActivityUiState
    data class Shown(
        val activities: List<LocationActivity>,
        override val eventSink: (ActivityEvent) -> Unit
    ): ActivityUiState, EventHandler<ActivityEvent>
}

sealed interface ActivityEvent : UiEvent {
    data object OnClick : ActivityEvent
}

sealed interface ActivitySideEffect : UiSideEffect {
}