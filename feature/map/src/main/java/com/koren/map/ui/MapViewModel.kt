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
import com.koren.common.models.user.UserData
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
import com.koren.common.util.Constants.DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ActivityRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetFamilyLocations
import com.koren.domain.UpdateUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
        if (!locationService.isLocationPermissionGranted()) return MapUiState.LocationPermissionNotGranted(onPermissionGranted = { setupLocationUpdates(currentUser?.locationUpdateFrequencyInMins) })
        if (currentUser == null) return MapUiState.Loading

        LaunchedEffect(Unit) { setupLocationUpdates(currentUser?.locationUpdateFrequencyInMins) }
        val targetUserIdFromNav = savedStateHandle.toRoute<MapDestination>().userId
        val familyMembers by getAllFamilyMembersUseCase().collectAsState(initial = emptyList())
        val savedLocations by getFamilyLocations().collectAsState(initial = emptyList())

        var selectedMarkerUserDataState by remember { mutableStateOf<UserData?>(null) }
        var followedUserIdState by remember { mutableStateOf<String?>(null) }

        val cameraPosition = rememberCameraPositionState(
            init = {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(
                        currentUser?.lastLocation?.latitude ?: 0.0,
                        currentUser?.lastLocation?.longitude ?: 0.0
                    ),
                    14.5f
                )
            }
        )

        LaunchedEffect(targetUserIdFromNav, familyMembers.size) {
            if (followedUserIdState != null) return@LaunchedEffect
            if (targetUserIdFromNav == null || familyMembers.isEmpty()) return@LaunchedEffect
            val userLocation = familyMembers.find { it.id == targetUserIdFromNav }?.lastLocation

            userLocation?.let {
                updateCameraPosition(
                    cameraPositionState = cameraPosition,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    zoom = 16f,
                    animate = true
                )
            }
        }

        LaunchedEffect(followedUserIdState, familyMembers) {
            if (followedUserIdState == null) return@LaunchedEffect
            val userToFollow = familyMembers.find { it.id == followedUserIdState }
            userToFollow?.lastLocation?.let { location ->
                updateCameraPosition(
                    cameraPositionState = cameraPosition,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    zoom = 16f,
                    animate = true
                )
            }
        }

        return MapUiState.Shown(
            familyMembers = familyMembers,
            savedLocations = savedLocations,
            cameraPosition = cameraPosition,
            selectedMarkerUserData = selectedMarkerUserDataState,
            followedUserId = followedUserIdState,
        ) { event ->
            when (event) {
                is MapEvent.FamilyMemberClicked -> {
                    selectedMarkerUserDataState = event.userData
                    event.userData.lastLocation?.let {
                        updateCameraPosition(
                            cameraPositionState = cameraPosition,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            zoom = 16f,
                            animate = true
                        )
                    }
                }
                is MapEvent.FollowUser -> {
                    followedUserIdState = event.userId
                    selectedMarkerUserDataState = null
                }
                is MapEvent.StopFollowing -> followedUserIdState = null
                is MapEvent.DismissMarkerActions -> selectedMarkerUserDataState = null
                is MapEvent.EditModeClicked -> _sideEffects.emitSuspended(MapSideEffect.NavigateToEditPlaces)
                is MapEvent.PinClicked -> {
                    selectedMarkerUserDataState = null
                    followedUserIdState = null
                    updateCameraPosition(
                        cameraPositionState = cameraPosition,
                        latitude = event.latitude,
                        longitude = event.longitude,
                        zoom = 16f,
                        animate = true
                    )
                }
            }
        }
    }

    private fun updateCameraPosition(
        cameraPositionState: CameraPositionState,
        latitude: Double,
        longitude: Double,
        zoom: Float,
        animate: Boolean
    ) {
        if (animate && cameraPositionState.isMoving) return

        val cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), zoom))
        if (animate) {
            viewModelScope.launch {
                try {
                    cameraPositionState.animate(
                        update = cameraUpdate,
                        durationMs = 1000
                    )
                } catch (e: Exception) {
                    Timber.e("Failed to animate camera position: $e")
                }
            }
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), zoom)
        }
    }

    private fun setupLocationUpdates(frequency: Int?) {
        viewModelScope.launch {
            locationService.requestLocationUpdates(frequency?: DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS).collect { location ->
                try {
                    updateUserLocationUseCase(location)
                    activityRepository.insertNewActivity(location)
                    Timber.d("User location updated: $location")
                } catch (e: Exception) {
                    Timber.d("Failed to update user location: $e")
                }
            }
        }
    }
}