@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.common.models.family.FamilyRole
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
    navigateToManageFamily: () -> Unit,
    navigateToChangePassword: () -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(ScaffoldState(isTopBarVisible = false))

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
            is AccountUiSideEffect.ShowMessage -> onShowSnackbar(uiSideEffect.message)
            is AccountUiSideEffect.NavigateToEditProfile -> navigateToEditProfile()
            is AccountUiSideEffect.NavigateToActivity -> navigateToActivity()
            is AccountUiSideEffect.NavigateToNotifications -> context.startActivity(settingsIntent, null)
            is AccountUiSideEffect.NavigateToManageFamily -> navigateToManageFamily()
            is AccountUiSideEffect.NavigateToChangePassword -> navigateToChangePassword()
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

    if (uiState.areYouSureDialogType != AreYouSureDialogType.None) {
        AreYouSureDialog(
            areYouSureActionInProgress = uiState.areYouSureActionInProgress,
            areYouSureDialogType = uiState.areYouSureDialogType,
            onConfirm = { uiState.eventSink(AccountUiEvent.ConfirmAreYouSureDialog) },
            onDismissRequest = { uiState.eventSink(AccountUiEvent.DismissAreYouSureDialog) }
        )
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
                    .data(uiState.userData.profilePictureUrl)
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

        options
            .filter {
                if (uiState.userData?.familyRole == FamilyRole.CHILD) !it.isChildRestricted
                else true
            }
            .forEach { option ->
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
private fun AreYouSureDialog(
    areYouSureActionInProgress: Boolean,
    areYouSureDialogType: AreYouSureDialogType,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    if (areYouSureActionInProgress) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = AlertDialogDefaults.shape
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Processing...",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        text = "Please hang tight while we process your request.",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LoadingIndicator()
                    Spacer(modifier = Modifier.height(48.dp))

                }
            }
        }
    } else {
        AlertDialog(
            title = {
                when (areYouSureDialogType) {
                    is AreYouSureDialogType.LeaveFamily -> Text("Leave Family")
                    is AreYouSureDialogType.DeleteFamilyMember -> Text("Delete Family")
                    is AreYouSureDialogType.DeleteAccount -> Text("Delete Account")
                    is AreYouSureDialogType.None -> Unit
                }
            },
            icon = {
                when (areYouSureDialogType) {
                    is AreYouSureDialogType.LeaveFamily -> Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Leave Family"
                    )
                    is AreYouSureDialogType.DeleteFamilyMember -> Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Family"
                    )
                    is AreYouSureDialogType.DeleteAccount -> Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Account"
                    )
                    is AreYouSureDialogType.None -> Unit
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirm
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
            },
            onDismissRequest = {
                onDismissRequest()
            },
            text = {
                when (areYouSureDialogType) {
                    is AreYouSureDialogType.LeaveFamily -> Text("Are you sure you want to leave the family? This action cannot be undone.")
                    is AreYouSureDialogType.DeleteFamilyMember -> Text("Are you sure you want to delete the family? This action cannot be undone.")
                    is AreYouSureDialogType.DeleteAccount -> Text("Are you sure you want to delete your account? This action cannot be undone.")
                    is AreYouSureDialogType.None -> Text("Are you sure?")
                }
            }
        )
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

@ThemePreview
@Composable
fun AreYouSureDialogPreview() {
    KorenTheme {
        AreYouSureDialog(
            areYouSureActionInProgress = false,
            areYouSureDialogType = AreYouSureDialogType.LeaveFamily("user123"),
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}

@ThemePreview
@Composable
fun AreYouSureDialogProcessingPreview() {
    KorenTheme {
        AreYouSureDialog(
            areYouSureActionInProgress = true,
            areYouSureDialogType = AreYouSureDialogType.LeaveFamily("user123"),
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}