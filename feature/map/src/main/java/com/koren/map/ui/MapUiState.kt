package com.koren.map.ui

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.UserData
import com.koren.common.models.UserLocation

sealed interface MapUiState {
    data object Loading : MapUiState
    data object LocationPermissionNotGranted : MapUiState

    data class Shown(
        val familyMembers: List<UserData> = emptyList(),
        val cameraPosition: CameraPositionState = CameraPositionState(),
        val eventSink: (MapEvent) -> Unit
    ): MapUiState
}

sealed interface MapEvent {
    data class MapPinClicked(val userData: UserData) : MapEvent
}

fun UserLocation.toLatLng() = LatLng(latitude, longitude)