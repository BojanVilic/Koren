package com.koren.map.ui.edit_places

import com.koren.common.models.family.SavedLocation
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface EditPlacesUiState : UiState {
    data object Loading : EditPlacesUiState
    data class Shown(
        val searchBarExpanded: Boolean = false,
        val searchQuery: String = "",
        val locationSuggestions: List<SuggestionResponse> = emptyList(),
        val familyLocations: List<SavedLocation>,
        override val eventSink: (EditPlacesUiEvent) -> Unit
    ) : EditPlacesUiState, EventHandler<EditPlacesUiEvent>
}

sealed interface EditPlacesUiEvent : UiEvent {
    data class OnExpandSearchBarChanged(val expanded: Boolean) : EditPlacesUiEvent
    data class SearchTextChanged(val text: String) : EditPlacesUiEvent
    data class LocationSuggestionClicked(val location: SuggestionResponse) : EditPlacesUiEvent
}

sealed interface EditPlacesUiSideEffect : UiSideEffect {
    data class NavigateToSaveLocation(val placeId: String) : EditPlacesUiSideEffect
}