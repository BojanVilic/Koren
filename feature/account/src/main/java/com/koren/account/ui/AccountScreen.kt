package com.koren.account.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.Destination
import com.koren.designsystem.components.SimpleSnackbar
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object AccountDestination : Destination

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onLogOut: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is AccountUiSideEffect.LogOut -> onLogOut()
            is AccountUiSideEffect.ShowError -> onShowSnackbar(uiSideEffect.message)
        }
    }

    AccountScreenContent(
        uiState = uiState
    )
}

@Composable
private fun AccountScreenContent(
    uiState: AccountUiState
) {

    when (uiState) {
        is AccountUiState.Loading -> CircularProgressIndicator()
        is AccountUiState.Shown -> AccountScreenShownContent(
            uiState = uiState
        )
    }
}

@Composable
private fun AccountScreenShownContent(
    uiState: AccountUiState.Shown
) {

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uiState.eventSink(AccountUiEvent.UploadNewProfilePicture(uri))
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        .size(24.dp)
                        .offset {
                            IntOffset(-16, -16)
                        }
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomEnd)
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
            AsyncImage(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(uiState.userData?.profilePictureUrl)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                uiState.eventSink(AccountUiEvent.LogOut)
            }
        ) {
            Text(text = "Log out")
        }
    }
}

@ThemePreview
@Composable
fun AccountScreenPreview() {
    KorenTheme {
        AccountScreenContent(
            uiState = AccountUiState.Shown(
                userData = null,
                eventSink = {}
            )
        )
    }
}