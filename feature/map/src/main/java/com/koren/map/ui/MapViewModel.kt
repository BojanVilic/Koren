@file:OptIn(ExperimentalCoroutinesApi::class)

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationService: LocationService,
    private val updateUserLocationUseCase: UpdateUserLocationUseCase,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase
): StateViewModel<MapEvent, MapUiState, MapSideEffect>() {

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

            viewModelScope.launch(Dispatchers.Default) {
                _sideEffects.asSharedFlow()
                    .filter { it is MapSideEffect.GetNewLocationSuggestions }
                    .debounce(300)
                    .flatMapLatest { locationService.getPlaceSuggestions((it as MapSideEffect.GetNewLocationSuggestions).newQuery) }
                    .collect { suggestions ->
                        val currentState = (_uiState.value as? MapUiState.Shown)?: return@collect
                        if (currentState.editMode) {
                            _uiState.update { currentState.copy(locationSuggestions = suggestions)}
                        }
                    }
            }
        }
    }

    override fun handleEvent(event: MapEvent) {
        withEventfulState<MapUiState.Shown> { current ->
            when (event) {
                is MapEvent.FamilyMemberClicked -> updateCameraPosition(current, event.userData)
                is MapEvent.EditModeClicked -> enterEditMode(current)
                is MapEvent.EditModeFinished -> exitEditMode(current)
                is MapEvent.SearchTextChanged -> {
                    _uiState.update { current.copy(searchQuery = event.text) }
                    _sideEffects.emitSuspended(MapSideEffect.GetNewLocationSuggestions(event.text))
                }
                is MapEvent.ExpandSearchBar -> _uiState.update { current.copy(searchBarExpanded = true) }
                is MapEvent.CollapseSearchBar -> _uiState.update { current.copy(searchBarExpanded = false) }
            }
        }
    }

    private fun enterEditMode(current: MapUiState.Shown) {
        _uiState.update { current.copy(editMode = true) }
        _sideEffects.emitSuspended(MapSideEffect.ShowEditMode)
    }

    private fun exitEditMode(current: MapUiState.Shown) {
        _uiState.update { current.copy(editMode = false) }
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