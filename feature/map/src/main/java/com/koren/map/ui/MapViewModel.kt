package com.koren.map.ui

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.UserData
import com.koren.common.services.LocationService
import com.koren.common.util.StateViewModel
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.UpdateUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationService: LocationService,
    private val updateUserLocationUseCase: UpdateUserLocationUseCase,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase
): StateViewModel<MapEvent, MapUiState, Nothing>() {

    override fun setInitialState(): MapUiState = MapUiState.Loading

    init {
        viewModelScope.launch {
            locationService.getLocation { result ->
                result.onSuccess { location ->
                    viewModelScope.launch(Dispatchers.Default) {
                        updateUserLocationUseCase(location)
                    }
                }
                result.onFailure {
                    Timber.d("Failed to get user location: $it")
                }
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            getAllFamilyMembersUseCase().collect {
                val firstMemberCameraPosition = it.firstNotNullOf { user -> user.lastLocation }
                _uiState.value = MapUiState.Shown(
                    familyMembers = it,
                    cameraPosition = CameraPositionState(position = CameraPosition.fromLatLngZoom(LatLng(firstMemberCameraPosition.latitude, firstMemberCameraPosition.longitude), 15f)),
                    eventSink = { event -> handleEvent(event) }
                )
            }
        }
    }

    override fun handleEvent(event: MapEvent) {
        withEventfulState<MapUiState.Shown> { current ->
            when (event) {
                is MapEvent.FamilyMemberClicked -> updateCameraPosition(current, event.userData)
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
}