@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.map.ui.edit_places

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.family.LocationIcon
import com.koren.common.models.family.SavedLocation
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object EditPlacesDestination

@Composable
fun EditPlacesScreen(
    viewModel: EditPlacesViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isTopBarVisible = false,
            isBottomBarVisible = false
        )
    )

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            else -> Unit
        }
    }

    EditPlacesScreenContent(
        uiState = uiState
    )

}

@Composable
private fun EditPlacesScreenContent(
    uiState: EditPlacesUiState
) {
    when (uiState) {
        is EditPlacesUiState.Loading -> CircularProgressIndicator()
        is EditPlacesUiState.Shown -> EditPlacesScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun EditPlacesScreenShownContent(
    uiState: EditPlacesUiState.Shown
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
//        BottomSheetDefaults.DragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
        SearchBar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = if (uiState.searchBarExpanded) Modifier else Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    query = uiState.searchQuery,
                    onQueryChange = { uiState.eventSink(EditPlacesUiEvent.SearchTextChanged(it)) },
                    onSearch = {
//                        onSearch(textFieldState.text.toString())
//                        expanded = false
                    },
                    expanded = uiState.searchBarExpanded,
                    onExpandedChange = { uiState.eventSink(EditPlacesUiEvent.OnExpandSearchBarChanged(it)) },
                    placeholder = { Text("Search") },
                    leadingIcon = if (uiState.searchBarExpanded) {
                        {
                            IconButton(
                                onClick = { uiState.eventSink(EditPlacesUiEvent.OnExpandSearchBarChanged(false)) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    } else null,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                )
            },
            expanded = uiState.searchBarExpanded,
            onExpandedChange = { uiState.eventSink(EditPlacesUiEvent.OnExpandSearchBarChanged(it)) },
            colors = SearchBarDefaults.colors(
                containerColor = BottomSheetDefaults.ContainerColor,
                dividerColor = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    items(uiState.locationSuggestions) { suggestion ->
                        ListItem(
                            modifier = Modifier
                                .clickable {
                                    uiState.eventSink(
                                        EditPlacesUiEvent.OnExpandSearchBarChanged(
                                            false
                                        )
                                    )
                                    uiState.eventSink(
                                        EditPlacesUiEvent.LocationSuggestionClicked(
                                            suggestion
                                        )
                                    )
                                }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            headlineContent = { Text(suggestion.primaryText) },
                            supportingContent = { Text(suggestion.secondaryText) },
                            leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Saved Locations",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                items(uiState.familyLocations) { locations ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(LocationIcon.fromString(locations.iconName).drawableResId),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = locations.name
                                )
                                Text(
                                    text = locations.address
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ThemePreview
@Composable
fun EditPlacesScreenPreview() {
    KorenTheme {
        EditPlacesScreenContent(
            uiState = EditPlacesUiState.Shown(
                familyLocations = listOf(
                    SavedLocation(
                        name = "Home",
                        address = "123 Main St, Springfield, USA",
                        latitude = 37.7749,
                        longitude = -122.4194,
                        iconName = LocationIcon.HOUSE.name
                    ),
                    SavedLocation(
                        name = "Work",
                        address = "456 Elm St, Springfield, USA",
                        latitude = 37.7749,
                        longitude = -122.4194,
                        iconName = LocationIcon.HOUSE_2.name
                    ),
                    SavedLocation(
                        name = "Gym",
                        address = "789 Oak St, Springfield, USA",
                        latitude = 37.7749,
                        longitude = -122.4194,
                        iconName = LocationIcon.HUT.name
                    )
                ),
                eventSink = {}
            )
        )
    }
}