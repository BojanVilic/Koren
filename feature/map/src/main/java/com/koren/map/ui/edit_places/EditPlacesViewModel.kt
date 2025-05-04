package com.koren.map.ui.edit_places

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.services.LocationService
import com.koren.common.util.MoleculeViewModel
import com.koren.domain.GetFamilyLocations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class EditPlacesViewModel @Inject constructor(
    private val locationService: LocationService,
    private val getFamilyLocations: GetFamilyLocations
    ): MoleculeViewModel<EditPlacesUiEvent, EditPlacesUiState, EditPlacesUiSideEffect>() {
    override fun setInitialState(): EditPlacesUiState = EditPlacesUiState.Loading

    @Composable
    override fun produceState(): EditPlacesUiState {
        val familyLocations by getFamilyLocations().collectAsState(initial = emptyList())

        var searchBarExpanded by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var suggestions by remember { mutableStateOf(emptyList<SuggestionResponse>()) }

        LaunchedEffect(searchQuery) {
            if (searchQuery.length > 3) {
                delay(300)
//            suggestions = locationService.getPlaceSuggestions(searchQuery)
                suggestions = getDummyLocationSuggestions()
            }
        }

        return EditPlacesUiState.Shown(
            searchBarExpanded = searchBarExpanded,
            searchQuery = searchQuery,
            locationSuggestions = suggestions,
            familyLocations = familyLocations
        ) { event ->
            when (event) {
                is EditPlacesUiEvent.OnExpandSearchBarChanged -> searchBarExpanded = event.expanded
                is EditPlacesUiEvent.SearchTextChanged -> searchQuery = event.text
                is EditPlacesUiEvent.LocationSuggestionClicked -> _sideEffects.emitSuspended(EditPlacesUiSideEffect.NavigateToSaveLocation(event.location.id))
            }
        }
    }

    private fun getDummyLocationSuggestions(): List<SuggestionResponse> {
        val suggestions = listOf(
            SuggestionResponse(
                id = "ChIJa7PjNOg1K4gREdFQwwrkG0Q",
                primaryText = "5550 McGrail Avenue",
                secondaryText = "Niagara Falls, ON, Canada",
                latitude = 43.094260528205254,
                longitude = -79.0765215277345
            ),
            SuggestionResponse(
                id = "ChIJa7PjNOg1K4gREdFQwwrkG0Q",
                primaryText = "6430 Montrose Road",
                secondaryText = "Niagara Falls, ON, Canada",
                latitude = 43.08167874422444,
                longitude = -79.1219035989796
            ),
            SuggestionResponse(
                id = "ChIJa7PjNOg1K4gREdFQwwrkG0Q",
                primaryText = "6767 Morrison St",
                secondaryText = "Niagara Falls, ON, Canada",
                latitude = 43.10512883109716,
                longitude = -79.10819105821295
            ),
            SuggestionResponse(
                id = "ChIJa7PjNOg1K4gREdFQwwrkG0Q",
                primaryText = "88 Queen St E",
                secondaryText = "Toronto, ON, Canada",
                latitude = 43.653225,
                longitude = -79.383186
            )
        )

        return suggestions
    }
}