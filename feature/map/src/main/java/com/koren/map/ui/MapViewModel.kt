package com.koren.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
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
                _state.value = MapUiState.Shown(
                    familyMembers = it,
                    eventSink = { event -> handleEvent(event) }
                )
            }
        }
    }

    private fun handleEvent(event: MapEvent) {
        withShownState { current ->
            when (event) {
                is MapEvent.MapPinClicked -> Unit
            }
        }
    }

    private inline fun withShownState(action: (MapUiState.Shown) -> Unit) {
        val currentState = _state.value
        if (currentState is MapUiState.Shown) {
            action(currentState)
        }
    }
}