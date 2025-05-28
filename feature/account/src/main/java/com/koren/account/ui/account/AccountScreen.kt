@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.account.ui.account

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.icon.ActivitySelected
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object AccountDestination

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onLogOut: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToActivity: () -> Unit,
    navigateToManageFamily: () -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(ScaffoldState(isTopBarVisible = false))

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Settings.EXTRA_APP_PACKAGE, LocalContext.current.packageName)
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is AccountUiSideEffect.LogOut -> onLogOut()
            is AccountUiSideEffect.ShowError -> onShowSnackbar(uiSideEffect.message)
            is AccountUiSideEffect.NavigateToEditProfile -> navigateToEditProfile()
            is AccountUiSideEffect.NavigateToActivity -> navigateToActivity()
            is AccountUiSideEffect.NavigateToNotifications -> context.startActivity(settingsIntent, null)
            is AccountUiSideEffect.NavigateToManageFamily -> navigateToManageFamily()
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
        is AccountUiState.Shown -> AccountScreenShownContent(uiState = uiState)
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

    uiState.optionContent?.let { bottomSheetContent ->
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = {
                uiState.eventSink(AccountUiEvent.CloseOption)
            }
        ) {
            bottomSheetContent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
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
                        .size(36.dp)
                        .offset {
                            IntOffset(-8, -8)
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

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = uiState.userData?.displayName?: "",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeaturedOptions(
                title = "Feedback",
                icon = Icons.AutoMirrored.Filled.Send,
                onClick = { uiState.eventSink(AccountUiEvent.SendFeedback) }
            )

            FeaturedOptions(
                title = "Premium",
                icon = Icons.Filled.Star,
                onClick = { uiState.eventSink(AccountUiEvent.Premium) }
            )

            FeaturedOptions(
                title = "Activity",
                icon = KorenIcons.ActivitySelected,
                onClick = { uiState.eventSink(AccountUiEvent.Activity) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(16.dp))

        options.forEach { option ->
            AccountOptionItem(
                option = option,
                onClick = { uiState.eventSink(option.event) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(16.dp),
            text = uiState.appVersion,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.FeaturedOptions(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .fillMaxHeight()
            .weight(1f),
        onClick = { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title
            )
        }
    }
}

@Composable
fun AccountOptionItem(
    option: AccountOption,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        option.icon?.let {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = it,
                contentDescription = null,
                tint = if (option.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (option.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            option.subText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@ThemePreview
@Composable
fun AccountScreenPreview() {
    KorenTheme {
        AccountScreenContent(
            uiState = AccountUiState.Shown(
                userData = UserData(),
                appVersion = "1.0.0",
                eventSink = {}
            )
        )
    }
}