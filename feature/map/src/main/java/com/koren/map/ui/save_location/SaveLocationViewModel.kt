package com.koren.map.ui.save_location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.koren.common.models.family.LocationIcon
import com.koren.common.models.family.SavedLocation
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.services.LocationService
import com.koren.common.util.MoleculeViewModel
import com.koren.domain.SaveLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SaveLocationViewModel @Inject constructor(
    private val locationService: LocationService,
    private val savedStateHandle: SavedStateHandle,
    private val saveLocationUseCase: SaveLocationUseCase
): MoleculeViewModel<SaveLocationUiEvent, SaveLocationUiState, SaveLocationUiSideEffect>() {

    override fun setInitialState(): SaveLocationUiState = SaveLocationUiState.Loading

    @Composable
    override fun produceState(): SaveLocationUiState {
        var saveLocationSuggestion by remember { mutableStateOf(SuggestionResponse()) }
        val placeId = savedStateHandle.toRoute<SaveLocationDestination>().placeId
        LaunchedEffect(placeId) {
            saveLocationSuggestion = locationService.getPlaceDetails(placeId) ?: throw IllegalStateException("Place details not found")
        }
        var locationName by remember { mutableStateOf("") }
        var locationIcon by remember { mutableStateOf(LocationIcon.DEFAULT) }

        return SaveLocationUiState.Shown(
            saveLocationSuggestion = saveLocationSuggestion,
            saveLocationIcon = locationIcon,
            saveLocationName = locationName
        ) { event ->
            when (event) {
                is SaveLocationUiEvent.SaveLocationClicked -> saveLocation(locationName, saveLocationSuggestion, locationIcon)
                is SaveLocationUiEvent.SaveLocationDismissed -> _sideEffects.emitSuspended(SaveLocationUiSideEffect.Dismiss)
                is SaveLocationUiEvent.SaveLocationIconChanged -> locationIcon = event.icon
                is SaveLocationUiEvent.SaveLocationNameChanged -> locationName = event.name
            }
        }
    }

    private fun saveLocation(
        name: String,
        saveLocation: SuggestionResponse,
        icon: LocationIcon
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val location = SavedLocation(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    address = saveLocation.primaryText + ", " + saveLocation.secondaryText,
                    latitude = saveLocation.latitude,
                    longitude = saveLocation.longitude,
                    iconName = icon.name
                )

                saveLocationUseCase(location)
                    .onSuccess { _sideEffects.emitSuspended(SaveLocationUiSideEffect.Dismiss) }
                    .onFailure { Timber.d("Failed to save location: $it") }
            } catch (e: Exception) {
                Timber.d("Failed to save location: $e")
            }
        }
    }
}