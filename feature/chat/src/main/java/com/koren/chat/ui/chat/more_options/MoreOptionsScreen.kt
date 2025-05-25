package com.koren.chat.ui.chat.more_options

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.chat.MessageType
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.icon.Copy
import com.koren.designsystem.icon.Delete
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class MoreOptionsDestination(val messageId: String)

@Composable
fun MoreOptionsScreen(
    viewModel: MoreOptionsViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(

        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel) { sideEffect ->
        when (sideEffect) {
            MoreOptionsUiSideEffect.NavigateBack -> navigateBack()
        }
    }

    MoreOptionsScreenContent(uiState)
}

@Composable
private fun MoreOptionsScreenContent(uiState: MoreOptionsUiState) {
    when (uiState) {
        is MoreOptionsUiState.Loading -> LoadingContent()
        is MoreOptionsUiState.Shown -> MoreOptionsScreenShownContent(uiState)
    }
}

@Composable
private fun MoreOptionsScreenShownContent(uiState: MoreOptionsUiState.Shown) {
    val reactions = listOf("👍", "❤️", "😂", "😮", "😢", "🙏")

    Column(
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            reactions.forEach { reaction ->
                FilledTonalIconButton(
                    onClick = {  }
                ) {
                    Text(
                        text = reaction,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Column {
            if (uiState.message.messageType == MessageType.TEXT) {
                val clipboardManager = LocalClipboard.current
                val coroutineScope = rememberCoroutineScope()

                MoreOptionsRow(
                    icon = KorenIcons.Copy,
                    text = "Copy",
                    onClick = {
                        coroutineScope.launch {
                            clipboardManager.setClipEntry(
                                clipEntry = ClipEntry(
                                    clipData = ClipData(
                                        "Copied Message",
                                        arrayOf("text/plain"),
                                        ClipData.Item(uiState.message.textContent ?: "")
                                    )
                                )
                            )
                        }
                    }
                )
            }
            MoreOptionsRow(
                icon = KorenIcons.Delete,
                text = "Delete",
                onClick = { uiState.eventSink(MoreOptionsUiEvent.DeleteMessage) },
                critical = true
            )
        }
    }
}

@Composable
private fun MoreOptionsRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    critical: Boolean = false
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = text,
            tint = if (critical) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = if (critical) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}

@ThemePreview
@Composable
private fun MoreOptionsScreenPreview() {
    KorenTheme {
        MoreOptionsScreenContent(
            MoreOptionsUiState.Shown(
                eventSink = {}
            )
        )
    }
}