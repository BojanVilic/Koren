@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.carfax.manage_familiy.edit_member

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.common.models.family.FamilyRole
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
data class EditMemberDestination(val memberId: String)

@Composable
fun EditMemberScreen(
    viewModel: EditMemberViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel) { sideEffect ->
        when (sideEffect) {
            else -> Unit
        }
    }

    EditMemberScreenContent(uiState)
}

@Composable
private fun EditMemberScreenContent(uiState: EditMemberUiState) {
    when (uiState) {
        is EditMemberUiState.Loading -> LoadingContent()
        is EditMemberUiState.Shown -> EditMemberScreenShownContent(uiState)
    }
}

@Composable
private fun EditMemberScreenShownContent(
    uiState: EditMemberUiState.Shown
) {
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val placeholder: Painter = remember { ColorPainter(primaryContainerColor) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetDefaults.DragHandle()
        AsyncImage(
            modifier = Modifier
                .size(96.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(true)
                .data(uiState.memberDetails.profilePictureUrl)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder
        )

        Text(
            text = uiState.memberDetails.displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
        Text(
            text = "Current Role: ${uiState.memberDetails.familyRole.name.lowercase().replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AnimatedVisibility(uiState.selectedRole == FamilyRole.CHILD) {
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 8.dp),
                    text = "Location Update Frequency (in minutes):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.locationUpdateFrequencyOptions.forEachIndexed { index, frequencyOption ->
                        ToggleButton(
                            modifier = Modifier
                                .weight(if (frequencyOption == uiState.selectedFrequency) 1.5f else 1f)
                                .semantics { role = Role.RadioButton },
                            checked = frequencyOption == uiState.selectedFrequency,
                            onCheckedChange = { uiState.eventSink(EditMemberUiEvent.UpdateLocationUpdateFrequency(frequencyOption)) },
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    uiState.locationUpdateFrequencyOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Text(
                                text = frequencyOption.toString(),
                                maxLines = if (frequencyOption == uiState.selectedFrequency) 2 else 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        val options = FamilyRole.entries.dropLast(1)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, familyRole ->
                ToggleButton(
                    modifier = Modifier
                        .weight(if (uiState.selectedRole == familyRole) 1.5f else 1f)
                        .semantics { role = Role.RadioButton },
                    checked = uiState.selectedRole == familyRole,
                    onCheckedChange = { uiState.eventSink(EditMemberUiEvent.UpdateFamilyRole(familyRole)) },
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Text(
                        text = familyRole.name.lowercase().replaceFirstChar { it.uppercase() },
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {  },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove Member",
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Remove Family Member",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@ThemePreview
@Composable
private fun EditMemberScreenPreview() {
    KorenTheme {
        EditMemberScreenContent(
            EditMemberUiState.Shown(
                memberDetails = UserData(
                    id = "123",
                    displayName = "John Doe",
                    profilePictureUrl = "https://example.com/profile.jpg",
                    familyRole = FamilyRole.CHILD
                ),
                selectedRole = FamilyRole.CHILD,
                eventSink = {}
            )
        )
    }
}