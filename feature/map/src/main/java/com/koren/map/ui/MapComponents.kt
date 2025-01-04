@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.map.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object MapDestination : Destination

@Composable
fun MapScreen() {

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

    BottomSheetScaffold(
        scaffoldState = scaffoldStateProvider,
        sheetContent = {
            ActionBottomSheetContent()
        }
    ) {
        MapScreenContent()
    }
}

@Composable
private fun MapScreenContent() {
    val curugSerbiaLatLng = LatLng(45.464563748965375, 20.04631224955966)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(curugSerbiaLatLng, 15f)
    }
    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            MarkerComposable(
                state = MarkerState(position = curugSerbiaLatLng)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary
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
            shape = RoundedCornerShape(8.dp)
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

@ThemePreview
@Composable
fun MapScreenPreview() {
    KorenTheme {
        MapScreenContent()
    }
}