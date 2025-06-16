@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.koren.map.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallExtendedFloatingActionButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.koren.common.models.activity.LocationActivity
import com.koren.common.models.family.LocationIcon
import com.koren.common.models.user.UserData
import com.koren.common.models.user.UserLocation
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.DateUtils.toRelativeTime
import com.koren.designsystem.components.InitialsAvatar
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Route
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
        sheetContent = { ActionBottomSheetContent(uiState) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = uiState.cameraPosition,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapLoaded = { mapReady = true },
                onMapClick = { uiState.eventSink(MapEvent.DismissMarkerActions) },
                mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
            ) {
                uiState.familyMembers.filter { it.lastLocation != null }.forEach { member ->
                    val currentLastLocation = member.lastLocation!!
                    val markerState = remember(member.id) {
                        MarkerState(
                            position = LatLng(currentLastLocation.latitude, currentLastLocation.longitude)
                        )
                    }

                    ProfilePicPin(
                        member = member,
                        targetLocation = currentLastLocation,
                        displayName = member.displayName,
                        imageUrl = member.profilePictureUrl,
                        onMarkerClicked = {
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                            uiState.eventSink(MapEvent.FamilyMemberClicked(it))
                        },
                        markerState = markerState,
                        mapReady = mapReady,
                        isSelectedForMenu = uiState.selectedMarkerUserData?.id == member.id
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

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.selectedMarkerUserData?.let { selectedUser ->
                    selectedUser.lastLocation?.let {
                        FamilyMemberLocationDetails(
                            modifier = Modifier.weight(1f),
                            userData = selectedUser,
                            onFollowClicked = { uiState.eventSink(MapEvent.FollowUser(selectedUser.id)) },
                            isFollowing = uiState.followedUserId == selectedUser.id,
                            lastLocation = uiState.lastUserLocationActivities[selectedUser.id]
                        )
                    }
                }
                if (uiState.selectedMarkerUserData == null) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    AnimatedVisibility(
                        visible = uiState.selectedMarkerUserData == null,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        SmallExtendedFloatingActionButton(
                            onClick = { uiState.eventSink(MapEvent.EditModeClicked) },
                            containerColor = BottomSheetDefaults.ContainerColor,
                            text = { Text(text = "Edit places") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit places"
                                )
                            },
                            expanded =
                                scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
                                        && uiState.selectedMarkerUserData == null
                        )
                    }
                    AnimatedVisibility(
                        visible = uiState.followedUserId != null,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = { uiState.eventSink(MapEvent.StopFollowing) },
                            icon = { Icon(Icons.Filled.Close, "Stop Following") },
                            text = { Text("Stop Following") },
                            expanded = uiState.selectedMarkerUserData == null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionBottomSheetContent(
    uiState: MapUiState.Shown
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
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
                InitialsAvatar(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            uiState.eventSink(MapEvent.FamilyMemberClicked(member))
                        },
                    imageUrl = member.profilePictureUrl,
                    name = member.displayName
                )
            }
        }
    }
}

@Composable
private fun FamilyMemberLocationDetails(
    modifier: Modifier = Modifier,
    userData: UserData,
    onFollowClicked: () -> Unit,
    isFollowing: Boolean,
    lastLocation: LocationActivity? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BottomSheetDefaults.ContainerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            InitialsAvatar(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                imageUrl = userData.profilePictureUrl,
                name = userData.displayName
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = userData.displayName,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = lastLocation?.let {
                        "\uD83D\uDCCD ${it.locationName}"
                    } ?: "\uD83D\uDCCD Location not available",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Last updated: ${lastLocation?.createdAt?.toRelativeTime()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )

                val batteryEmoji = if (userData.batteryLevel > 20) "\uD83D\uDD0B" else "\uD83E\uDEAB"

                Text(
                    text = "5.2 km away from you | $batteryEmoji ${userData.batteryLevel}% battery",
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (isFollowing.not()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = onFollowClicked
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = 8.dp),
                            imageVector = KorenIcons.Route,
                            contentDescription = "Follow User",
                        )
                        Text(
                            text = "Follow ${userData.displayName}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
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
    member: UserData,
    targetLocation: UserLocation,
    displayName: String,
    imageUrl: String?,
    onMarkerClicked: (UserData) -> Unit,
    markerState: MarkerState,
    mapReady: Boolean,
    isSelectedForMenu: Boolean
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .allowHardware(false)
            .build()
    )
    val painterState by painter.state.collectAsState()

    val targetLatLng = LatLng(targetLocation.latitude, targetLocation.longitude)

    LaunchedEffect(targetLatLng, markerState) {
        val startPosition = markerState.position
        if (startPosition.latitude == targetLatLng.latitude && startPosition.longitude == targetLatLng.longitude) return@LaunchedEffect
        val animatable = Animatable(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        ) {
            val fraction = value.toDouble()
            val currentLat = lerpDouble(startPosition.latitude, targetLatLng.latitude, fraction)
            val currentLng = lerpDouble(startPosition.longitude, targetLatLng.longitude, fraction)
            markerState.position = LatLng(currentLat, currentLng)
        }
        if (markerState.position.latitude != targetLatLng.latitude || markerState.position.longitude != targetLatLng.longitude) {
            markerState.position = targetLatLng
        }
    }

    MarkerComposable(
        keys = arrayOf(displayName, painterState, mapReady, isSelectedForMenu),
        state = markerState,
        title = displayName,
        onClick = {
            onMarkerClicked(member)
            true
        }
    ) {
        PinImage(
            imageUrl = imageUrl,
            displayName = displayName,
            painter = painter,
            isSelected = isSelectedForMenu
        )
    }
}

private fun lerpDouble(start: Double, stop: Double, fraction: Double): Double {
    return start + fraction * (stop - start)
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
    painter: AsyncImagePainter,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 86.dp)
            .clip(PinShape)
            .border(
                width = if (isSelected) 4.dp else 3.dp,
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                shape = PinShape
            )
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
        val saneHeadHeightToWidthRatio = 0.6f.coerceIn(0.1f, 0.75f)
        val actualHeadHeight = size.width * saneHeadHeightToWidthRatio
        val polygonVertices = floatArrayOf(
            size.width / 2f, size.height,
            0f, actualHeadHeight,
            size.width / 2f, 0f,
            size.width, actualHeadHeight
        )
        val perVertexRounding = listOf(
            CornerRounding(radius = 0f, smoothing = 0f),
            CornerRounding(radius = actualHeadHeight, smoothing = 0f),
            CornerRounding(radius = actualHeadHeight, smoothing = 0f),
            CornerRounding(radius = actualHeadHeight, smoothing = 0f)
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
            ),
            isSelected = false
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
                selectedMarkerUserData = UserData(
                    id = "1",
                    displayName = "John Doe",
                    profilePictureUrl = "https://example.com/profile.jpg",
                    lastLocation = UserLocation(37.7749, -122.4194),
                    batteryLevel = 24
                ),
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
private fun MarkerActionsMenuPreview() {
    KorenTheme {
        FamilyMemberLocationDetails(
            userData = UserData(
                id = "1",
                displayName = "John Doe",
                profilePictureUrl = "https://example.com/profile.jpg",
                lastLocation = UserLocation(37.7749, -122.4194)
            ),
            onFollowClicked = {},
            isFollowing = false
        )
    }
}