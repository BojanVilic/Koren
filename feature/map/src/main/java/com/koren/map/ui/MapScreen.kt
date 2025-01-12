@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.koren.map.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.koren.common.models.UserData
import com.koren.common.models.UserLocation
import com.koren.common.util.Destination
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object MapDestination : Destination

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = hiltViewModel()
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(isTopBarVisible = false)
    )

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

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

    LaunchedEffect((uiState as? MapUiState.Shown)?.cameraPosition?.isMoving) {
        if ((uiState as? MapUiState.Shown)?.cameraPosition?.isMoving == true) {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            (uiState as? MapUiState.Shown)?.let { ActionBottomSheetContent(it) }
        },
        sheetPeekHeight = 112.dp
    ) {
        Box {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = uiState.cameraPosition,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {

                uiState.familyMembers.filter { it.lastLocation != null }.forEach { member ->
                    Pin(
                        imageUrl = member.profilePictureUrl,
                        displayName = member.displayName,
                        location = member.lastLocation?: UserLocation(),
                        onClick = {
                            uiState.eventSink(MapEvent.FamilyMemberClicked(member))
                        }
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
        Icons.Default.Add,
        Icons.Default.ThumbUp,
        Icons.Default.DateRange,
        Icons.Default.Create
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(actions) { actionItem ->
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = actionItem,
                        contentDescription = "Action"
                    )
                }
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
            items(uiState.familyMembers) { member ->
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
    imageUrl: String?,
    displayName: String,
    location: UserLocation,
    onClick: () -> Unit
) {
    val markerState = remember { MarkerState(position = LatLng(location.latitude, location.longitude)) }
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
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
        PinImage(
            imageUrl = imageUrl,
            displayName = displayName,
            painter = painter
        )
    }
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
            uiState = MapUiState.Shown(
                familyMembers = listOf(
                    UserData(
                        id = "1",
                        displayName = "John Doe",
                        lastLocation = UserLocation(37.7749, -122.4194)
                    )
                ),
                eventSink = {}
            )
        )
    }
}