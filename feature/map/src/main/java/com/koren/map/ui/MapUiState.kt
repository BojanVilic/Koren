package com.koren.map.ui

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.UserData
import com.koren.common.models.UserLocation
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface MapUiState : UiState {
    data object Loading : MapUiState
    data class LocationPermissionNotGranted(val onPermissionGranted: () -> Unit) : MapUiState

    data class Shown(
        val familyMembers: List<UserData> = emptyList(),
        val cameraPosition: CameraPositionState = CameraPositionState(),
        val editMode: Boolean = false,
        val searchQuery: String = "",
        val searchBarExpanded: Boolean = false,
        val locationSuggestions: List<Pair<String, String>> = emptyList(),
        override val eventSink: (MapEvent) -> Unit
    ): MapUiState, EventHandler<MapEvent>
}

sealed interface MapEvent : UiEvent {
    data class FamilyMemberClicked(val userData: UserData) : MapEvent
    data object EditModeClicked : MapEvent
    data class SearchTextChanged(val text: String) : MapEvent
    data object EditModeFinished : MapEvent
    data object ExpandSearchBar : MapEvent
    data object CollapseSearchBar : MapEvent
}

sealed interface MapSideEffect : UiSideEffect {
    data object ShowEditMode : MapSideEffect
    data object CloseEditMode : MapSideEffect
    data class GetNewLocationSuggestions(val newQuery: String) : MapSideEffect
}

fun UserLocation.toLatLng() = LatLng(latitude, longitude)