package com.koren.map.ui

import com.koren.common.models.UserData

sealed interface MapUiState {
    data object Loading : MapUiState
    data object LocationPermissionNotGranted : MapUiState

    data class Shown(
        val familyMembers: List<UserData> = emptyList(),
        val eventSink: (MapEvent) -> Unit
    ): MapUiState
}

sealed interface MapEvent {
    data class MapPinClicked(val userData: UserData) : MapEvent
}