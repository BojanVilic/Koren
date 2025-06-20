@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.koren.account.ui.edit_profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.common.models.family.FamilyRole
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.RotateCamera
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object EditProfileDestination

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            title = "Edit Profile",
            isTopBarVisible = true
        )
    )

    DisposableEffectWithLifecycle(
        onCreate = { viewModel.init() }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is EditProfileSideEffect.ShowSnackbar -> onShowSnackbar(uiSideEffect.message)
        }
    }

    EditProfileContent(uiState = uiState)
}

@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState
) {
    when (uiState) {
        is EditProfileUiState.Loading -> LoadingContent()
        is EditProfileUiState.Shown -> ShownContent(uiState = uiState)
    }
}

@Composable
private fun ShownContent(
    uiState: EditProfileUiState.Shown
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                uiState.eventSink(EditProfileUiEvent.UploadNewProfilePicture(it))
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.newProfilePicture == null) {
            if (uiState.userData?.profilePictureUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.size(128.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add photo",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .offset {
                                IntOffset(-8, -8)
                            }
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .clickable {
                                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
            else {
                Box(
                    modifier = Modifier.size(128.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                            .clickable {
                                imagePicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        model = ImageRequest.Builder(LocalContext.current)
                            .crossfade(true)
                            .data(uiState.userData?.profilePictureUrl)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )

                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .offset {
                                IntOffset(-8, -8)
                            }
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .clickable {
                                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        imageVector = KorenIcons.RotateCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.size(128.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .clickable {
                            imagePicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(uiState.newProfilePicture)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Icon(
                    modifier = Modifier
                        .size(36.dp)
                        .offset {
                            IntOffset(-8, -8)
                        }
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .clickable {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    imageVector = KorenIcons.RotateCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 16.dp),
            value = uiState.userData?.displayName?: "",
            onValueChange = { uiState.eventSink(EditProfileUiEvent.OnNameChange(it)) },
            label = { Text(text = "Name") }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 16.dp),
            value = uiState.userData?.email?: "",
            onValueChange = {},
            label = { Text(text = "Email") },
            enabled = false
        )

        if (uiState.userData?.familyRole != FamilyRole.CHILD) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 16.dp)
            ) {
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
                                .weight(if (frequencyOption == uiState.userData?.locationUpdateFrequencyInMins) 1.5f else 1f)
                                .semantics { role = Role.RadioButton },
                            checked = frequencyOption == uiState.userData?.locationUpdateFrequencyInMins,
                            onCheckedChange = { uiState.eventSink(EditProfileUiEvent.UpdateLocationUpdateFrequency(frequencyOption)) },
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
                                maxLines = if (frequencyOption == uiState.userData?.locationUpdateFrequencyInMins) 2 else 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 16.dp),
            onClick = { uiState.eventSink(EditProfileUiEvent.SaveProfile) }
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@ThemePreview
@Composable
private fun EditProfilePreview() {
    KorenTheme {
        EditProfileContent(
            uiState = EditProfileUiState.Shown(
                userData = UserData(
                    displayName = "John Doe",
                    familyRole = FamilyRole.PARENT
                ),
                eventSink = {}
            )
        )
    }
}