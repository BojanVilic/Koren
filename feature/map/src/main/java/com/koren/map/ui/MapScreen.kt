@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.koren.map.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState
import com.koren.common.models.family.LocationIcon
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
    onNavigateToEditPlaces: () -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(isTopBarVisible = false)
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = mapViewModel
    ) { sideEffect ->
        when (sideEffect) {
            is MapSideEffect.ShowSnackbar -> onShowSnackbar(sideEffect.message)
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

    LaunchedEffect(uiState.cameraPosition.isMoving) {
        if (uiState.cameraPosition.isMoving) {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        }
    }

    var mapReady by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            ActionBottomSheetContent(uiState)
        },
        sheetPeekHeight = 128.dp
    ) {

        Box {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = uiState.cameraPosition,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapLoaded = { mapReady = true }
            ) {
                uiState.familyMembers.filter { it.lastLocation != null }.forEach { member ->
                    val markerState = rememberMarkerState(
                        position = LatLng(member.lastLocation?.latitude?: 0.0, member.lastLocation?.longitude?: 0.0)
                    )
                    ProfilePicPin(
                        imageUrl = member.profilePictureUrl,
                        displayName = member.displayName,
                        location = member.lastLocation ?: UserLocation(),
                        onClick = {
                            uiState.eventSink(MapEvent.FamilyMemberClicked(member))
                        },
                        markerState = markerState,
                        mapReady = mapReady
                    )
                }

                uiState.savedLocations.forEach { location ->
                    SavedLocationPin(
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
                        },
                        mapReady = mapReady
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionBottomSheetContent(
    uiState: MapUiState.Shown
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
private fun SavedLocationPin(
    @DrawableRes imageResource: Int,
    displayName: String,
    latitude: Double,
    longitude: Double,
    onClick: () -> Unit,
    mapReady: Boolean
) {
    val markerState = remember { MarkerState(position = LatLng(latitude, longitude)) }
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageResource)
            .allowHardware(false)
            .build()
    )
    val painterState by painter.state.collectAsState()

    MarkerComposable(
        keys = arrayOf(displayName, painterState, mapReady),
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
private fun ProfilePicPin(
    imageUrl: String?,
    displayName: String,
    location: UserLocation,
    onClick: () -> Unit,
    markerState: MarkerState,
    mapReady: Boolean
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .allowHardware(false)
            .build()
    )
    val painterState by painter.state.collectAsState()

    MarkerComposable(
        keys = arrayOf(location.latitude, location.longitude, displayName, painterState, mapReady),
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

    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 86.dp)
            .clip(PinShape)
            .border(3.dp, MaterialTheme.colorScheme.primary, PinShape)
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

object PinShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // Ensure the ratio is within a reasonable range to prevent visual issues.
        val saneHeadHeightToWidthRatio = 0.6f.coerceIn(0.1f, 0.75f)

        // Calculate the actual height of the curved 'head' part of the pin.
        // This is measured from the very top of the pin (y=0) to the "shoulders".
        val actualHeadHeight = size.width * saneHeadHeightToWidthRatio

        // Vertices of the base polygon (before rounding)
        // P0: Bottom tip
        // P1: Left "shoulder" (y-coordinate is actualHeadHeight)
        // P2: Top-center of the arc (y-coordinate is 0f)
        // P3: Right "shoulder" (y-coordinate is actualHeadHeight)
        val polygonVertices = floatArrayOf(
            size.width / 2f, size.height,        // P0 (Tip)
            0f, actualHeadHeight,                // P1 (Left Shoulder)
            size.width / 2f, 0f,                 // P2 (Top-Center of Arc, at y=0)
            size.width, actualHeadHeight         // P3 (Right Shoulder)
        )

        // The rounding radius for the top corners (P1, P2, P3).
        // To make these corners form a continuous smooth curve that defines the head,
        // the radius should generally be equal to the actualHeadHeight.
        val topCornerRoundingRadius = actualHeadHeight

        val perVertexRounding = listOf(
            CornerRounding(radius = 0f, smoothing = 0f),              // P0 (Tip) - Sharp corner
            CornerRounding(radius = topCornerRoundingRadius, smoothing = 0f), // P1 (Left Shoulder)
            CornerRounding(radius = topCornerRoundingRadius, smoothing = 0f), // P2 (Top-Center)
            CornerRounding(radius = topCornerRoundingRadius, smoothing = 0f)  // P3 (Right Shoulder)
        )

        val roundedPolygon = RoundedPolygon(
            vertices = polygonVertices,
            perVertexRounding = perVertexRounding
        )

        val composePath: Path = roundedPolygon.toPath().asComposePath()
        return Outline.Generic(composePath)
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
            uiState = MapUiState.Shown(
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