@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.koren.map.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.koren.common.models.UserData
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
        ScaffoldState(
            isTopBarVisible = false
        )
    )

    val scaffoldStateProvider = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded
        )
    )

    val cameraPermissionState = rememberPermissionState(ACCESS_FINE_LOCATION)

    if (cameraPermissionState.status.isGranted) {
        Text("Location permission Granted")
    } else {
        LaunchedEffect(cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val uiState by mapViewModel.state.collectAsStateWithLifecycle()

    BottomSheetScaffold(
        scaffoldState = scaffoldStateProvider,
        sheetContent = {
            ActionBottomSheetContent()
        }
    ) {
        MapScreenContent(uiState = uiState)
    }
}

@Composable
private fun MapScreenContent(uiState: MapUiState) {
    when (uiState) {
        is MapUiState.Loading -> LoadingContent()
        is MapUiState.LocationPermissionNotGranted -> Text("Location permission must be granted!")
        is MapUiState.Shown -> ShownContent(uiState)
    }
}

@Composable
fun ShownContent(uiState: MapUiState.Shown) {

    val firstMemberCameraPosition = uiState.familyMembers.firstNotNullOf { it.lastLocation }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(firstMemberCameraPosition.latitude, firstMemberCameraPosition.longitude), 15f)
    }
    val coroutineScope = rememberCoroutineScope()

    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {

            uiState.familyMembers.filter { it.lastLocation != null }.forEach { member ->
                Pin(
                    imageUrl = member.profilePictureUrl,
                    displayName = member.displayName,
                    location = member.lastLocation?: com.koren.common.models.LatLng(),
                    onClick = {
                        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                it,
                                18f
                            )
                        )

                        coroutineScope.launch {
                            cameraPositionState.animate(
                                update = cameraUpdate,
                                durationMs = 1000
                            )
                        }
                    }
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
                .fillMaxWidth(),
            value = "",
            onValueChange = { },
            label = { Text("Search") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Composable
fun ActionBottomSheetContent() {
    val actions = listOf(
        Icons.Default.Add,
        Icons.Default.ThumbUp,
        Icons.Default.DateRange,
        Icons.Default.Create
    )

    LazyRow(
        modifier = Modifier.padding(8.dp)
    ) {
        items(actions) { actionItem ->
            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = actionItem,
                    contentDescription = "Action"
                )
            }
        }
    }
}

@Composable
fun Pin(
    imageUrl: String?,
    displayName: String,
    location: com.koren.common.models.LatLng,
    onClick: (LatLng) -> Unit
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
            onClick(markerState.position)
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
fun PinImagePreview() {
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
fun MapScreenPreview() {
    KorenTheme {
        MapScreenContent(
            uiState = MapUiState.Shown(
                familyMembers = listOf(
                    UserData(
                        id = "1",
                        displayName = "John Doe",
                        lastLocation = com.koren.common.models.LatLng(37.7749, -122.4194)
                    )
                ),
                eventSink = {}
            )
        )
    }
}