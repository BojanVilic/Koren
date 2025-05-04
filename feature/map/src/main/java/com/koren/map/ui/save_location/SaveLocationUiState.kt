package com.koren.map.ui.save_location

import com.koren.common.models.family.LocationIcon
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface SaveLocationUiState : UiState {
    data object Loading : SaveLocationUiState
    data class Shown(
        val saveLocationSuggestion: SuggestionResponse = SuggestionResponse(),
        val saveLocationIcon: LocationIcon = LocationIcon.DEFAULT,
        val saveLocationName: String = "",
        override val eventSink: (SaveLocationUiEvent) -> Unit
    ) : SaveLocationUiState, EventHandler<SaveLocationUiEvent>
}

sealed interface SaveLocationUiEvent : UiEvent {
    data object SaveLocationClicked : SaveLocationUiEvent
    data object SaveLocationDismissed : SaveLocationUiEvent
    data class SaveLocationNameChanged(val name: String) : SaveLocationUiEvent
    data class SaveLocationIconChanged(val icon: LocationIcon) : SaveLocationUiEvent
}

sealed interface SaveLocationUiSideEffect : UiSideEffect {
    data object Dismiss : SaveLocationUiSideEffect
}