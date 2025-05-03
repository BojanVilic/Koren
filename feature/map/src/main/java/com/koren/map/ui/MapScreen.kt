@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.koren.map.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState
import com.koren.common.models.family.LocationIcon
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.user.UserData
import com.koren.common.models.user.UserLocation
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.ActionButton
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.models.ActionItem
import com.koren.designsystem.models.IconResource
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.map.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class MapDestination(
    val userId: String? = null
)

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit,
    userId: String?,
    onNavigateToEditPlaces: () -> Unit
) {

    LaunchedEffect(Unit) {
        mapViewModel.init(userId = userId)
    }

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(isTopBarVisible = false)
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = mapViewModel
    ) { sideEffect ->
        when (sideEffect) {
            is MapSideEffect.ShowSnackbar -> onShowSnackbar(sideEffect.message)
            is MapSideEffect.GetNewLocationSuggestions -> TODO()
            is MapSideEffect.NavigateToEditPlaces -> onNavigateToEditPlaces()
        }
    }

    MapScreenContent(uiState = uiState)
}

@Composable
private fun MapScreenContent(
    uiState: MapUiState
) {
    when (uiState) {
        is MapUiState.Loading -> LoadingContent()
        is MapUiState.LocationPermissionNotGranted -> PermissionNotGrantedContent(uiState)
        is MapUiState.Shown -> ShownContent(uiState = uiState)
    }
}

@Composable
private fun LocationPermissionPermanentDenial() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Keep your family in the loop and know where everyone is by enabling location sharing.",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Your privacy is protected: your location is always kept private and secure, and only shared with your family.",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
            },
        ) {
            Text("Enable Location Sharing")
        }
    }
}

@Composable
private fun PermissionNotGrantedContent(uiState: MapUiState.LocationPermissionNotGranted) {
    val fineLocationPermissionState = rememberPermissionState(ACCESS_FINE_LOCATION)

    LaunchedEffect(fineLocationPermissionState.status.isGranted) {
        if (fineLocationPermissionState.status.isGranted)
            uiState.onPermissionGranted()
        else
            fineLocationPermissionState.launchPermissionRequest()
    }

    LocationPermissionPermanentDenial()
}

@Composable
private fun ShownContent(
    uiState: MapUiState.Shown
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            confirmValueChange = {
                it != SheetValue.Hidden
            }
        )
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scaffoldState.bottomSheetState.expand()
    }

    if (uiState is MapUiState.Shown.IdleMap) {
        LaunchedEffect(uiState.cameraPosition.isMoving) {
            if (uiState.cameraPosition.isMoving) {
                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            when (uiState) {
                is MapUiState.Shown.SearchMode -> PlacesSearchBar(uiState)
                is MapUiState.Shown.SaveLocation -> SaveLocation(uiState)
                is MapUiState.Shown.IdleMap -> ActionBottomSheetContent(uiState)
            }
        },
        sheetPeekHeight = 128.dp
    ) {

        Box {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = uiState.cameraPosition,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                uiState.familyMembers.filter { it.lastLocation != null }.forEach { member ->
                    val markerState = rememberMarkerState(
                        position = LatLng(member.lastLocation?.latitude?: 0.0, member.lastLocation?.longitude?: 0.0)
                    )
                    Pin(
                        imageUrl = member.profilePictureUrl,
                        displayName = member.displayName,
                        location = member.lastLocation ?: UserLocation(),
                        onClick = {
                            uiState.eventSink(MapEvent.FamilyMemberClicked(member))
                        },
                        markerState = markerState
                    )
                }

                uiState.savedLocations.forEach { location ->
                    Pin(
                        imageResource = LocationIcon.fromString(location.iconName).drawableResId,
                        displayName = location.name,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        onClick = {
                            uiState.eventSink(
                                MapEvent.PinClicked(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlacesSearchBar(
    uiState: MapUiState.Shown.SearchMode
) {

    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = "Search for a place",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            text = "Never wonder where your family is: save their frequent places.",
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = uiState.searchQuery,
                    onQueryChange = { uiState.eventSink(MapEvent.SearchTextChanged(it)) },
                    onSearch = { uiState.eventSink(MapEvent.CollapseSearchBar) },
                    expanded = uiState.searchBarExpanded,
                    onExpandedChange = {
                        if (it) uiState.eventSink(MapEvent.ExpandSearchBar)
                        else uiState.eventSink(MapEvent.CollapseSearchBar)
                    },
                    placeholder = { Text("Home, work, gym... address") },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                uiState.eventSink(MapEvent.EditModeFinished)
                            },
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                )
            },
            expanded = uiState.searchBarExpanded,
            onExpandedChange = {
                if (it) uiState.eventSink(MapEvent.ExpandSearchBar)
                else uiState.eventSink(MapEvent.CollapseSearchBar)
            },
            colors = SearchBarDefaults.colors(
                containerColor = BottomSheetDefaults.ContainerColor
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    items(uiState.locationSuggestions) { suggestion ->
                        ListItem(
                            modifier = Modifier
                                .clickable {
                                    uiState.eventSink(MapEvent.CollapseSearchBar)
                                    uiState.eventSink(MapEvent.LocationSuggestionClicked(suggestion))
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
    }
}

@Composable
private fun SaveLocation(
    uiState: MapUiState.Shown.SaveLocation
) {
    val icons = LocationIcon.entries
    val quickPickNames = listOf(
        stringResource(R.string.home),
        stringResource(R.string.work),
        stringResource(R.string.school),
        stringResource(R.string.gym),
        stringResource(R.string.grocery_store),
        stringResource(R.string.park)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(36.dp),
                painter = painterResource(uiState.saveLocationIcon.drawableResId),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = uiState.saveLocationSuggestion.primaryText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPickNames) { name ->
                AssistChip(
                    label = { Text(text = name) },
                    onClick = { uiState.eventSink(MapEvent.SaveLocationNameChanged(name)) },
                )
            }
        }
        
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = uiState.saveLocationName,
            onValueChange = { uiState.eventSink(MapEvent.SaveLocationNameChanged(it)) },
            label = { Text(text = stringResource(R.string.location_name)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        ) {
            items(icons) { icon ->
                IconButton(
                    onClick = { uiState.eventSink(MapEvent.SaveLocationIconChanged(icon)) }
                ) {
                    Image(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(icon.drawableResId),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            onClick = { uiState.eventSink(MapEvent.SaveLocationClicked) }
        ) {
            Text(
                text = stringResource(R.string.save_label)
            )
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            onClick = { uiState.eventSink(MapEvent.SaveLocationDismissed) }
        ) {
            Text(
                text = stringResource(R.string.cancel_label)
            )
        }
    }
}

@Composable
private fun ActionBottomSheetContent(
    uiState: MapUiState.Shown.IdleMap
) {

    val actions = listOf(
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Edit),
            text = "Edit places",
            onClick = { uiState.eventSink(MapEvent.EditModeClicked) }
        )
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(actions) { actionItem ->
                ActionButton(actionItem)
            }
        }

        HorizontalDivider()
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            text = "Look for family members",
            style = MaterialTheme.typography.labelLarge
        )

        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = uiState.familyMembers,
                key = { it.id }
            ) { member ->
                AsyncImage(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            uiState.eventSink(MapEvent.FamilyMemberClicked(member))
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(member.profilePictureUrl)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun Pin(
    @DrawableRes imageResource: Int,
    displayName: String,
    latitude: Double,
    longitude: Double,
    onClick: () -> Unit
) {
    val markerState = remember { MarkerState(position = LatLng(latitude, longitude)) }
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageResource)
            .allowHardware(false)
            .build()
    )

    MarkerComposable(
        keys = arrayOf(displayName, painter.state),
        state = markerState,
        title = displayName,
        onClick = {
            onClick()
            true
        }
    ) {
        PinIcon(painter = painter)
    }
}

@Composable
private fun Pin(
    imageUrl: String?,
    displayName: String,
    location: UserLocation,
    onClick: () -> Unit,
    markerState: MarkerState
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .allowHardware(false)
            .build()
    )

    LaunchedEffect(location.latitude, location.longitude) {
        markerState.position = LatLng(location.latitude, location.longitude)
    }

    MarkerComposable(
        keys = arrayOf(location.latitude, location.longitude, displayName, painter.state),
        state = markerState,
        title = displayName,
        onClick = {
            onClick()
            true
        }
    ) {
        PinImage(
            imageUrl = imageUrl,
            displayName = displayName,
            painter = painter
        )
    }
}

@Composable
private fun PinIcon(
    painter: AsyncImagePainter
) {
    Image(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .size(64.dp),
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun PinImage(
    imageUrl: String?,
    displayName: String,
    painter: AsyncImagePainter
) {
    val shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 0.dp)

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            Image(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxSize(),
                painter = painter,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = displayName.take(1).uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@ThemePreview
@Composable
private fun PinImagePreview() {
    KorenTheme {
        PinImage(
            imageUrl = null,
            displayName = "John Doe",
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(null)
                    .allowHardware(false)
                    .build()
            )
        )
    }
}

@ThemePreview
@Composable
private fun MapScreenPreview() {
    KorenTheme {
        MapScreenContent(
            uiState = MapUiState.Shown.IdleMap(
                familyMembers = listOf(
                    UserData(
                        id = "1",
                        displayName = "John Doe",
                        lastLocation = UserLocation(37.7749, -122.4194)
                    ),
                ),
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
private fun SaveLocationDialogPreview() {
    KorenTheme {
        SaveLocation(
            uiState = MapUiState.Shown.SaveLocation(
                saveLocationSuggestion = SuggestionResponse(
                    primaryText = "5550 McGrail Ave",
                    secondaryText = "Niagara Falls, ON, Canada"
                ),
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
private fun PlacesSearchBarPreview() {
    KorenTheme {
        PlacesSearchBar(
            uiState = MapUiState.Shown.SearchMode(
                searchQuery = "Search text",
                searchBarExpanded = true,
                locationSuggestions = listOf(
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
                ),
                eventSink = {}
            )
        )
    }
}