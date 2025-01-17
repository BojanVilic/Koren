package com.koren.map.ui

import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.family.LocationIcon
import com.koren.common.models.family.SavedLocation
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface MapUiState : UiState {
    data object Loading : MapUiState
    data class LocationPermissionNotGranted(val onPermissionGranted: () -> Unit) : MapUiState

    sealed interface Shown : MapUiState, EventHandler<MapEvent> {
        val cameraPosition: CameraPositionState
        val familyMembers: List<UserData>
        val savedLocations : List<SavedLocation>

        data class SearchMode(
            val searchQuery: String = "",
            val searchBarExpanded: Boolean = false,
            val locationSuggestions: List<SuggestionResponse> = emptyList(),
            override val cameraPosition: CameraPositionState = CameraPositionState(),
            override val familyMembers: List<UserData> = emptyList(),
            override val savedLocations: List<SavedLocation> = emptyList(),
            override val eventSink: (MapEvent) -> Unit
        ): Shown

        data class SaveLocation(
            val saveLocationSuggestion: SuggestionResponse = SuggestionResponse(),
            val saveLocationIcon: LocationIcon = LocationIcon.DEFAULT,
            val saveLocationName: String = "",
            override val cameraPosition: CameraPositionState = CameraPositionState(),
            override val familyMembers: List<UserData> = emptyList(),
            override val savedLocations: List<SavedLocation> = emptyList(),
            override val eventSink: (MapEvent) -> Unit
        ): Shown

        data class IdleMap(
            override val cameraPosition: CameraPositionState = CameraPositionState(),
            override val familyMembers: List<UserData> = emptyList(),
            override val savedLocations: List<SavedLocation> = emptyList(),
            override val eventSink: (MapEvent) -> Unit
        ): Shown
    }
}

sealed interface MapEvent : UiEvent {
    data class FamilyMemberClicked(val userData: UserData) : MapEvent
    data class PinClicked(val latitude: Double, val longitude: Double) : MapEvent
    data object EditModeClicked : MapEvent
    data class SearchTextChanged(val text: String) : MapEvent
    data class LocationSuggestionClicked(val location: SuggestionResponse) : MapEvent
    data object EditModeFinished : MapEvent
    data object ExpandSearchBar : MapEvent
    data object CollapseSearchBar : MapEvent
    data object SaveLocationClicked : MapEvent
    data object SaveLocationDismissed : MapEvent
    data class SaveLocationNameChanged(val name: String) : MapEvent
    data class SaveLocationIconChanged(val icon: LocationIcon) : MapEvent
}

sealed interface MapSideEffect : UiSideEffect {
    data class GetNewLocationSuggestions(val newQuery: String) : MapSideEffect
    data class ShowSnackbar(val message: String) : MapSideEffect
}