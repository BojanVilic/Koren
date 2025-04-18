@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.onboarding.ui.create_family

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.onboarding.R

@Composable
internal fun AddHouseAddressStep(
    uiState: CreateFamilyUiState.Step
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        AnimatedVisibility(
            visible = uiState.searchBarExpanded.not()
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(R.string.add_house_address_title),
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = stringResource(R.string.add_house_address_subtitle),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            expanded = uiState.searchBarExpanded,
            onExpandedChange = {
                uiState.eventSink(CreateFamilyEvent.ExpandSearchBar)
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = uiState.searchQuery,
                onValueChange = { uiState.eventSink(CreateFamilyEvent.SearchTextChanged(it)) },
                placeholder = { Text("Home address") },
                leadingIcon = if (uiState.searchBarExpanded || uiState.searchQuery.isNotBlank()) {
                    {
                        IconButton(
                            onClick = {
                                uiState.eventSink(CreateFamilyEvent.CollapseSearchBar)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                } else null,
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            ExposedDropdownMenu(
                expanded = uiState.searchBarExpanded && uiState.locationSuggestions.isNotEmpty(),
                onDismissRequest = {
                    uiState.eventSink(CreateFamilyEvent.CollapseSearchBar)
                }
            ) {
                uiState.locationSuggestions.forEach { suggestion ->
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                uiState.eventSink(CreateFamilyEvent.CollapseSearchBar)
                                uiState.eventSink(
                                    CreateFamilyEvent.LocationSuggestionClicked(
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

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.house_address_privacy_note), // Add this string resource
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@ThemePreview
@Composable
private fun AddHouseAddressStepPreview() {
    KorenTheme {
        AddHouseAddressStep(
            uiState = CreateFamilyUiState.Step(
                addressText = "Dositejeva 2, 21000 Novi Sad",
                eventSink = {}
            )
        )
    }
}