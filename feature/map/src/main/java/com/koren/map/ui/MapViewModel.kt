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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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
        if (!locationService.isLocationPermissionGranted()) {
            _uiState.update {
                MapUiState.LocationPermissionNotGranted(
                    onPermissionGranted = {
                        _uiState.update { MapUiState.Shown(eventSink = ::handleEvent) }
                    }
                )
            }
        } else {
            viewModelScope.launch {
                locationService.requestLocationUpdates().collect { location ->
                    try {
                        updateUserLocationUseCase(location)
                    } catch (e: Exception) {
                        Timber.d("Failed to update user location: $e")
                    }
                }
            }

            viewModelScope.launch(Dispatchers.Default) {
                getAllFamilyMembersUseCase()
                    .catch { _uiState.update { MapUiState.Shown(eventSink = ::handleEvent) } }
                    .collect { familyMembers ->
                        if (familyMembers.all { it.lastLocation == null }) {
                            _uiState.update { MapUiState.Shown(eventSink = ::handleEvent) }
                            return@collect
                        }
                        val firstMemberCameraPosition = familyMembers.firstNotNullOf { user -> user.lastLocation }
                        _uiState.update {
                            MapUiState.Shown(
                                familyMembers = familyMembers,
                                cameraPosition = CameraPositionState(position = CameraPosition.fromLatLngZoom(LatLng(firstMemberCameraPosition.latitude, firstMemberCameraPosition.longitude), 15f)),
                                eventSink = { event -> handleEvent(event) }
                            )
                        }
                    }
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