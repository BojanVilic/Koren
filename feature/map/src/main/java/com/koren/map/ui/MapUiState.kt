package com.koren.map.ui

import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.family.SavedLocation
import com.koren.common.models.user.UserData
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface MapUiState : UiState {
    data object Loading : MapUiState
    data class LocationPermissionNotGranted(val onPermissionGranted: () -> Unit) : MapUiState
    data class Shown(
        val cameraPosition: CameraPositionState = CameraPositionState(),
        val familyMembers: List<UserData> = emptyList(),
        val savedLocations: List<SavedLocation> = emptyList(),
        val isMapReady: Boolean = false,
        override val eventSink: (MapEvent) -> Unit
    ): MapUiState, EventHandler<MapEvent>
}

sealed interface MapEvent : UiEvent {
    data class FamilyMemberClicked(val userData: UserData) : MapEvent
    data class PinClicked(val latitude: Double, val longitude: Double) : MapEvent
    data object EditModeClicked : MapEvent
}

sealed interface MapSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : MapSideEffect
    data object NavigateToEditPlaces : MapSideEffect
}