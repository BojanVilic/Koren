@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.home.ui.home.member_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.icon.CallHome
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.MapSelected
import com.koren.designsystem.icon.Task
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable


@Serializable
data class MemberDetailsDestination(
    val userId: String
)

@Composable
fun MemberDetailsScreen(
    viewModel: MemberDetailsViewModel = hiltViewModel(),
    userId: String,
    navigateAndFindOnMap: (userId: String) -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.init(userId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is MemberDetailsUiSideEffect.NavigateAndFindOnMap -> navigateAndFindOnMap(uiSideEffect.userId)
            is MemberDetailsUiSideEffect.ShowSnackbarMessage -> Unit
        }
    }

    MemberDetailsScreenContent(
        uiState = uiState
    )
}

@Composable
private fun MemberDetailsScreenContent(
    uiState: MemberDetailsUiState
) {
    when (uiState) {
        is MemberDetailsUiState.Loading -> CircularProgressIndicator()
        is MemberDetailsUiState.Shown -> MemberDetailsScreenShownContent(uiState = uiState)
        is MemberDetailsUiState.SelfDetails -> SelfDetailsContent()
    }
}

@Composable
private fun MemberDetailsScreenShownContent(
    uiState: MemberDetailsUiState.Shown
) {
    Column(
        modifier = Modifier
            .clip(BottomSheetDefaults.ExpandedShape)
            .background(MaterialTheme.colorScheme.inverseSurface)
    ) {
        Row(
            modifier = Modifier
                .clip(BottomSheetDefaults.ExpandedShape)
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(MaterialTheme.colorScheme.inverseSurface),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = KorenIcons.MapSelected,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inverseOnSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Crossfade(
                targetState = uiState.distanceText
            ) { distanceText ->
                Text(
                    text = distanceText,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Column(
            modifier = Modifier
                .clip(BottomSheetDefaults.ExpandedShape)
                .fillMaxWidth()
                .background(BottomSheetDefaults.ContainerColor)
        ) {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            uiState.options.forEach { option ->
                Option(
                    icon = option.icon,
                    title = option.title,
                    description = option.description,
                    isEnabled = option.isEnabled,
                    onClick = {
                        if (option.isEnabled) {
                            uiState.eventSink(option.event)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Option(
    icon: ImageVector,
    title: String,
    description: String?,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val contentAlpha = if (isEnabled) 1f else 0.5f

    Row(
        modifier = Modifier
            .alpha(contentAlpha)
            .clickable(enabled = isEnabled) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            AnimatedVisibility(!description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SelfDetailsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetDefaults.DragHandle()
        Text(
            text = getRandomMessageAndEmoji(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

fun getRandomMessageAndEmoji(): String {
    val messagesAndEmojis = listOf(
        "Remember, spoons are just tiny shovels. 🥄",
        "Are your socks mismatched? 🧦",
        "Did you check behind the couch? 🕵️‍♂️",
        "You're the captain of this ship! 🚢",
        "Looking good, you! 😎",
        "You're the star of the show! 🌟",
        "Making things happen, as always! 🚀",
        "You're the boss! 👑",
        "You got this! 💪",
    )
    return messagesAndEmojis.random()
}

@ThemePreview
@Composable
fun MemberDetailsScreenShownContentPreview() {
    KorenTheme {
        Surface {
            MemberDetailsScreenShownContent(
                uiState = MemberDetailsUiState.Shown(
                    distanceText = "450m away",
                    options = listOf(
                        MemberDetailsOption(
                            icon = KorenIcons.CallHome,
                            title = "Call home",
                            event = MemberDetailsUiEvent.CallHome,
                            isEnabled = false,
                            description = "Request sent"
                        ),
                        MemberDetailsOption(
                            icon = KorenIcons.MapSelected,
                            title = "Find on map",
                            event = MemberDetailsUiEvent.FindOnMap
                        ),
                        MemberDetailsOption(
                            icon = KorenIcons.Task,
                            title = "View assigned tasks",
                            event = MemberDetailsUiEvent.ViewAssignedTasks
                        ),
                        MemberDetailsOption(
                            icon = Icons.Default.Edit,
                            title = "Edit role",
                            event = MemberDetailsUiEvent.EditRole
                        )
                    ),
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
fun SelfDetailsContentPreview() {
    KorenTheme {
        Surface {
            SelfDetailsContent()
        }
    }
}