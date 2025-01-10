package com.koren.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.UserData
import com.koren.common.services.LocationService
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.UpdateUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationService: LocationService,
    private val updateUserLocationUseCase: UpdateUserLocationUseCase,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase
): ViewModel() {

    private val _state = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val state: StateFlow<MapUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            locationService.getLocation { result ->
                result.onSuccess { location ->
                    viewModelScope.launch(Dispatchers.Default) {
                        updateUserLocationUseCase(location)
                    }
                }
                result.onFailure {
                    Timber.d("PROBAVANJE greska: $it")
                }
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            getAllFamilyMembersUseCase().collect {
                val firstMemberCameraPosition = it.firstNotNullOf { user -> user.lastLocation }
                _state.value = MapUiState.Shown(
                    familyMembers = it,
                    cameraPosition = CameraPositionState(position = CameraPosition.fromLatLngZoom(LatLng(firstMemberCameraPosition.latitude, firstMemberCameraPosition.longitude), 15f)),
                    eventSink = { event -> handleEvent(event) }
                )
            }
        }
    }

    private fun handleEvent(event: MapEvent) {
        withShownState { current ->
            when (event) {
                is MapEvent.MapPinClicked -> updateCameraPosition(current, event.userData)
            }
        }
    }

    private fun updateCameraPosition(current: MapUiState.Shown, userData: UserData) {
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition.fromLatLngZoom(
                userData.lastLocation?.toLatLng()?: LatLng(0.0, 0.0),
                18f
            )
        )

        viewModelScope.launch {
            current.cameraPosition.animate(
                update = cameraUpdate,
                durationMs = 1000
            )
        }
    }

    private inline fun withShownState(action: (MapUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is MapUiState.Shown) {
            action(currentState)
        }
    }
}