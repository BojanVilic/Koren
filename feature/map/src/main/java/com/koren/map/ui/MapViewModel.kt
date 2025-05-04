package com.koren.map.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.koren.common.models.user.UserLocation
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ActivityRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetFamilyLocations
import com.koren.domain.UpdateUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationService: LocationService,
    private val updateUserLocationUseCase: UpdateUserLocationUseCase,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase,
    private val getFamilyLocations: GetFamilyLocations,
    private val userSession: UserSession,
    private val activityRepository: ActivityRepository,
    private val savedStateHandle: SavedStateHandle
): MoleculeViewModel<MapEvent, MapUiState, MapSideEffect>() {

    override fun setInitialState(): MapUiState = MapUiState.Loading

    @Composable
    override fun produceState(): MapUiState {
        val currentUser by userSession.currentUser.collectAsState(initial = null)
        if (!locationService.isLocationPermissionGranted()) return MapUiState.LocationPermissionNotGranted(onPermissionGranted = { setupLocationUpdates() })
        if (currentUser == null) return MapUiState.Loading

        setupLocationUpdates()
        val targetUserId = savedStateHandle.toRoute<MapDestination>().userId
        val familyMembers by getAllFamilyMembersUseCase().collectAsState(initial = emptyList())
        val savedLocations by getFamilyLocations().collectAsState(initial = emptyList())
        val cameraPosition = rememberCameraPositionState(
            init = {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(
                        currentUser?.lastLocation?.latitude ?: 0.0,
                        currentUser?.lastLocation?.longitude ?: 0.0
                    ),
                    18f
                )
            }
        )

        LaunchedEffect(targetUserId, familyMembers.size) {
            if (targetUserId == null || familyMembers.isEmpty()) return@LaunchedEffect
            val userLocation = familyMembers.find { it.id == targetUserId }?.lastLocation

            updateCameraPosition(
                cameraPositionState = cameraPosition,
                latitude = userLocation?.latitude?: 0.0,
                longitude = userLocation?.longitude?: 0.0
            )
        }

        return MapUiState.Shown(
            familyMembers = familyMembers,
            savedLocations = savedLocations,
            cameraPosition = cameraPosition
        ) { event ->
            when (event) {
                is MapEvent.FamilyMemberClicked -> updateCameraPosition(
                    cameraPositionState = cameraPosition,
                    latitude = event.userData.lastLocation?.latitude?: 0.0,
                    longitude = event.userData.lastLocation?.longitude?: 0.0
                )
                is MapEvent.EditModeClicked -> _sideEffects.emitSuspended(MapSideEffect.NavigateToEditPlaces)
                is MapEvent.PinClicked -> updateCameraPosition(
                    cameraPositionState = cameraPosition,
                    latitude = event.latitude,
                    longitude = event.longitude
                )
            }
        }
    }

    private fun updateCameraPosition(
        cameraPositionState: CameraPositionState,
        latitude: Double,
        longitude: Double
    ) {
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 18f)
        )

        viewModelScope.launch {
            cameraPositionState.animate(
                update = cameraUpdate,
                durationMs = 1000
            )
        }
    }

    private fun setupLocationUpdates() {
        viewModelScope.launch {
            locationService.requestLocationUpdates().collect { location ->
                try {
                    updateUserLocationUseCase(location)
                    activityRepository.insertNewActivity(location)
                } catch (e: Exception) {
                    Timber.d("Failed to update user location: $e")
                }
            }
        }
    }
}