@file:OptIn(ExperimentalCoroutinesApi::class)

package com.koren.map.ui

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.koren.common.models.family.SavedLocation
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetFamilyLocations
import com.koren.domain.SaveLocationUseCase
import com.koren.domain.UpdateUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
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
    private val getFamilyLocations: GetFamilyLocations,
    private val userSession: UserSession
): StateViewModel<MapEvent, MapUiState, MapSideEffect>() {

    override fun setInitialState(): MapUiState = MapUiState.Loading

    init {
        if (!locationService.isLocationPermissionGranted()) {
            _uiState.update {
                MapUiState.LocationPermissionNotGranted(
                    onPermissionGranted = {
                        setupLocationUpdates()
                        fetchFamilyData()
                        observeLocationSuggestions()
                    }
                )
            }
        } else {
            setupLocationUpdates()
            fetchFamilyData()
            observeLocationSuggestions()
        }
    }

    override fun handleEvent(event: MapEvent) {
        withEventfulState<MapUiState.Shown.IdleMap> { current ->
            when (event) {
                is MapEvent.FamilyMemberClicked -> updateCameraPosition(
                    current = current,
                    latitude = event.userData.lastLocation?.latitude?: 0.0,
                    longitude = event.userData.lastLocation?.longitude?: 0.0
                )
                is MapEvent.EditModeClicked -> enterEditMode(current)
                is MapEvent.PinClicked -> updateCameraPosition(
                    current = current,
                    latitude = event.latitude,
                    longitude = event.longitude
                )
                else -> Unit
            }
        }
    }

    private fun handleSearchModeEvents(event: MapEvent) {
        withEventfulState<MapUiState.Shown.SearchMode> { current ->
            when (event) {
                is MapEvent.EditModeFinished -> exitEditMode(current)
                is MapEvent.SearchTextChanged -> {
                    _uiState.update { current.copy(searchQuery = event.text) }
                    _sideEffects.emitSuspended(MapSideEffect.GetNewLocationSuggestions(event.text))
                }
                is MapEvent.ExpandSearchBar -> _uiState.update { current.copy(searchBarExpanded = true) }
                is MapEvent.CollapseSearchBar -> _uiState.update { current.copy(searchBarExpanded = false) }
                is MapEvent.LocationSuggestionClicked -> _uiState.update { MapUiState.Shown.SaveLocation(
                    cameraPosition = current.cameraPosition,
                    familyMembers = current.familyMembers,
                    savedLocations = current.savedLocations,
                    saveLocationSuggestion = event.location,
                    eventSink = ::handleSaveLocationEvents
                ) }
                else -> Unit
            }
        }
    }

    private fun handleSaveLocationEvents(event: MapEvent) {
        withEventfulState<MapUiState.Shown.SaveLocation> { current ->
            when (event) {
                is MapEvent.SaveLocationClicked -> saveLocation(current)
                is MapEvent.SaveLocationDismissed -> _uiState.update { MapUiState.Shown.IdleMap(
                    cameraPosition = current.cameraPosition,
                    familyMembers = current.familyMembers,
                    savedLocations = current.savedLocations,
                    eventSink = ::handleEvent
                ) }
                is MapEvent.SaveLocationNameChanged -> _uiState.update { current.copy(saveLocationName = event.name) }
                is MapEvent.SaveLocationIconChanged -> _uiState.update { current.copy(saveLocationIcon = event.icon) }
                else -> Unit
            }
        }
    }

    private fun saveLocation(current: MapUiState.Shown.SaveLocation) {
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
                        _sideEffects.emitSuspended(MapSideEffect.ShowSnackbar("Location saved!"))
                    }
                    .onFailure { Timber.d("Failed to save location: $it") }
            } catch (e: Exception) {
                Timber.d("Failed to save location: $e")
            }
        }
    }

    private fun enterEditMode(current: MapUiState.Shown.IdleMap) {
        _uiState.update { MapUiState.Shown.SearchMode(
            cameraPosition = current.cameraPosition,
            familyMembers = current.familyMembers,
            savedLocations = current.savedLocations,
            eventSink = ::handleSearchModeEvents
        ) }
    }

    private fun exitEditMode(current: MapUiState.Shown) {
        _uiState.update { MapUiState.Shown.IdleMap(
            cameraPosition = current.cameraPosition,
            familyMembers = current.familyMembers,
            savedLocations = current.savedLocations,
            eventSink = ::handleEvent
        ) }
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

    private fun setupLocationUpdates() {
        viewModelScope.launch {
            locationService.requestLocationUpdates().collect { location ->
                try {
                    updateUserLocationUseCase(location)
                } catch (e: Exception) {
                    Timber.d("Failed to update user location: $e")
                }
            }
        }
    }

    private fun fetchFamilyData() {
        viewModelScope.launch(Dispatchers.Default) {
            combine(
                getAllFamilyMembersUseCase(),
                getFamilyLocations()
            ) { familyMembers, savedLocations ->
                familyMembers to savedLocations
            }
                .catch { _uiState.update { MapUiState.Shown.IdleMap(eventSink = ::handleEvent) } }
                .collect { (familyMembers, savedLocations) ->
                    val userLocation = userSession.currentUser.first().lastLocation
                    val current = (_uiState.value as? MapUiState.Shown)
                    if (current != null) {
                        _uiState.update {
                            MapUiState.Shown.IdleMap(
                                familyMembers = familyMembers,
                                savedLocations = savedLocations,
                                cameraPosition = current.cameraPosition,
                                eventSink = { event -> handleEvent(event) }
                            )
                        }
                        exitEditMode(current)
                    } else {
                        _uiState.update {
                            MapUiState.Shown.IdleMap(
                                familyMembers = familyMembers,
                                cameraPosition = CameraPositionState(
                                    position = CameraPosition.fromLatLngZoom(
                                        LatLng(
                                            userLocation?.latitude?: 0.0,
                                            userLocation?.longitude?: 0.0
                                        ),
                                        15f
                                    )
                                ),
                                savedLocations = savedLocations,
                                eventSink = { event -> handleEvent(event) }
                            )
                        }
                    }
                }
        }
    }

    private fun observeLocationSuggestions() {
        viewModelScope.launch(Dispatchers.Default) {
            _sideEffects.asSharedFlow()
                .debounce(300)
                .filterIsInstance<MapSideEffect.GetNewLocationSuggestions>()
                .flatMapLatest {
                    //                        locationService.getPlaceSuggestions(it.newQuery)
                    getDummyLocationSuggestions()
                }
                .collect { suggestions ->
                    val currentState =
                        (_uiState.value as? MapUiState.Shown.SearchMode) ?: return@collect
                    _uiState.update { currentState.copy(locationSuggestions = suggestions) }
                }
        }
    }

    private fun getDummyLocationSuggestions(): Flow<List<SuggestionResponse>> {
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

        return flowOf(suggestions)
    }
}