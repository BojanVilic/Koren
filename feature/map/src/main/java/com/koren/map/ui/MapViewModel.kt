@file:OptIn(ExperimentalCoroutinesApi::class)

package com.koren.map.ui

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.family.SavedLocation
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.user.UserData
import com.koren.common.services.LocationService
import com.koren.common.util.StateViewModel
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetFamilyLocations
import com.koren.domain.SaveLocationUseCase
import com.koren.domain.UpdateUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationService: LocationService,
    private val updateUserLocationUseCase: UpdateUserLocationUseCase,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val getFamilyLocations: GetFamilyLocations
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
                combine(
                    getAllFamilyMembersUseCase(),
                    getFamilyLocations()
                ) { familyMembers, savedLocations ->
                    familyMembers to savedLocations
                }
                .catch { _uiState.update { MapUiState.Shown(eventSink = ::handleEvent) } }
                .collect { (familyMembers, savedLocations) ->
                    if (familyMembers.all { it.lastLocation == null }) {
                        _uiState.update { MapUiState.Shown(eventSink = ::handleEvent) }
                        return@collect
                    }
                    val firstMemberCameraPosition = familyMembers.firstNotNullOf { user -> user.lastLocation }
                    if (_uiState.value is MapUiState.Shown) {
                        _uiState.update {
                            (_uiState.value as MapUiState.Shown).copy(
                                familyMembers = familyMembers,
                                savedLocations = savedLocations,
                                eventSink = { event -> handleEvent(event) }
                            )
                        }
                        exitEditMode(_uiState.value as MapUiState.Shown)
                    } else {
                        _uiState.update {
                            MapUiState.Shown(
                                familyMembers = familyMembers,
                                cameraPosition = CameraPositionState(position = CameraPosition.fromLatLngZoom(LatLng(firstMemberCameraPosition.latitude, firstMemberCameraPosition.longitude), 15f)),
                                savedLocations = savedLocations,
                                eventSink = { event -> handleEvent(event) }
                            )
                        }
                    }
                }
            }

            viewModelScope.launch(Dispatchers.Default) {
                _sideEffects.asSharedFlow()
                    .filter { it is MapSideEffect.GetNewLocationSuggestions }
                    .debounce(300)
                    .flatMapLatest {
//                        locationService.getPlaceSuggestions((it as MapSideEffect.GetNewLocationSuggestions).newQuery)
                        val suggestions = listOf(
                            SuggestionResponse(
                                primaryText = "5550 McGrail Avenue",
                                secondaryText = "Niagara Falls, ON, Canada",
                                latitude = 43.094260528205254,
                                longitude = -79.0765215277345
                            ),
                            SuggestionResponse(
                                primaryText = "6430 Montrose Road",
                                secondaryText = "Niagara Falls, ON, Canada",
                                latitude = 43.08167874422444,
                                longitude = -79.1219035989796
                            ),
                            SuggestionResponse(
                                primaryText = "6767 Morrison St",
                                secondaryText = "Niagara Falls, ON, Canada",
                                latitude = 43.10512883109716,
                                longitude = -79.10819105821295
                            )
                        )

                        flowOf(suggestions)

                    }
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
                is MapEvent.FamilyMemberClicked -> updateCameraPosition(
                    current = current,
                    latitude = event.userData.lastLocation?.latitude?: 0.0,
                    longitude = event.userData.lastLocation?.longitude?: 0.0
                )
                is MapEvent.EditModeClicked -> enterEditMode(current)
                is MapEvent.EditModeFinished -> exitEditMode(current)
                is MapEvent.SearchTextChanged -> {
                    _uiState.update { current.copy(searchQuery = event.text) }
                    _sideEffects.emitSuspended(MapSideEffect.GetNewLocationSuggestions(event.text))
                }
                is MapEvent.ExpandSearchBar -> _uiState.update { current.copy(searchBarExpanded = true) }
                is MapEvent.CollapseSearchBar -> _uiState.update { current.copy(searchBarExpanded = false) }
                is MapEvent.LocationSuggestionClicked -> _uiState.update { current.copy(saveLocationShown = true, saveLocationSuggestion = event.location) }
                is MapEvent.SaveLocationClicked -> saveLocation(current)
                is MapEvent.SaveLocationDismissed -> _uiState.update { current.copy(saveLocationShown = false) }
                is MapEvent.SaveLocationNameChanged -> _uiState.update { current.copy(saveLocationName = event.name) }
                is MapEvent.SaveLocationIconChanged -> _uiState.update { current.copy(saveLocationIcon = event.icon) }
                is MapEvent.PinClicked -> updateCameraPosition(
                    current = current,
                    latitude = event.latitude,
                    longitude = event.longitude
                )
            }
        }
    }

    private fun saveLocation(current: MapUiState.Shown) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val location = SavedLocation(
                    id = UUID.randomUUID().toString(),
                    name = current.saveLocationName,
                    address = current.saveLocationSuggestion.primaryText + ", " + current.saveLocationSuggestion.secondaryText,
                    latitude = current.saveLocationSuggestion.latitude,
                    longitude = current.saveLocationSuggestion.longitude,
                    iconName = current.saveLocationIcon.name
                )

                saveLocationUseCase(location)
                    .onSuccess {
//                        exitEditMode(current)
                        _sideEffects.emitSuspended(MapSideEffect.ShowSnackbar("Location saved!"))
                    }
                    .onFailure { Timber.d("Failed to save location: $it") }
            } catch (e: Exception) {
                Timber.d("Failed to save location: $e")
            }
        }
    }

    private fun enterEditMode(current: MapUiState.Shown) {
        _uiState.update { current.copy(editMode = true) }
    }

    private fun exitEditMode(current: MapUiState.Shown) {
        _uiState.update { current.copy(editMode = false, saveLocationShown = false) }
    }

    private fun updateCameraPosition(
        current: MapUiState.Shown,
        latitude: Double,
        longitude: Double
    ) {
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition.fromLatLngZoom(
                LatLng(latitude, longitude),
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