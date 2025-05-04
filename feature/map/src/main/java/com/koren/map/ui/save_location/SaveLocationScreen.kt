package com.koren.map.ui.save_location

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.family.LocationIcon
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.map.R
import kotlinx.serialization.Serializable

@Serializable
data class SaveLocationDestination(val placeId: String)

@Composable
fun SaveLocationScreen(
    viewModel: SaveLocationViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            SaveLocationUiSideEffect.Dismiss -> onDismiss()
        }
    }

    SaveLocationScreenContent(
        uiState = uiState
    )
}

@Composable
private fun SaveLocationScreenContent(
    uiState: SaveLocationUiState
) {
    when (uiState) {
        is SaveLocationUiState.Loading -> CircularProgressIndicator()
        is SaveLocationUiState.Shown -> SaveLocationScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun SaveLocationScreenShownContent(
    uiState: SaveLocationUiState.Shown
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column {
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
                            onClick = { uiState.eventSink(SaveLocationUiEvent.SaveLocationNameChanged(name)) },
                        )
                    }
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = uiState.saveLocationName,
                    onValueChange = { uiState.eventSink(SaveLocationUiEvent.SaveLocationNameChanged(it)) },
                    label = { Text(text = stringResource(R.string.location_name)) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                LazyRow(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                ) {
                    items(icons) { icon ->
                        IconButton(
                            onClick = { uiState.eventSink(SaveLocationUiEvent.SaveLocationIconChanged(icon)) }
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
                    onClick = { uiState.eventSink(SaveLocationUiEvent.SaveLocationClicked) }
                ) {
                    Text(
                        text = stringResource(R.string.save_label)
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    onClick = { uiState.eventSink(SaveLocationUiEvent.SaveLocationDismissed) }
                ) {
                    Text(
                        text = stringResource(R.string.cancel_label)
                    )
                }
            }
        }
    }
}

@ThemePreview
@Composable
fun SaveLocationScreenPreview() {
    KorenTheme {
        SaveLocationScreenContent(
            uiState = SaveLocationUiState.Shown(
                eventSink = {}
            )
        )
    }
}