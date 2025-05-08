package com.koren.chat.ui.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.koren.chat.ui.ChatUiEvent
import com.koren.chat.ui.ChatUiState
import com.koren.chat.ui.model.AttachmentOptions
import com.koren.designsystem.components.Scrim
import com.koren.designsystem.icon.Files
import com.koren.designsystem.icon.Image
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Video
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

@Composable
internal fun AttachmentsOverlay(
    uiState: ChatUiState.Shown,
    imagePicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {

    val hapticFeedback = LocalHapticFeedback.current
    var kickOffAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kickOffAnimation = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scrim(onClick = { uiState.eventSink(ChatUiEvent.CloseAttachmentsOverlay) })

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomEnd),
            visible = kickOffAnimation,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = 16.dp, vertical = 64.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    val attachments = listOf(
                        AttachmentOptions(
                            icon = KorenIcons.Image,
                            title = "Image",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        ),
                        AttachmentOptions(
                            icon = KorenIcons.Video,
                            title = "Video",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                            }
                        ),
                        AttachmentOptions(
                            icon = KorenIcons.Files,
                            title = "File",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                            }
                        )
                    )

                    attachments.forEachIndexed { index, attachmentItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    attachmentItem.onClick()
                                    uiState.eventSink(ChatUiEvent.CloseAttachmentsOverlay)
                                }
                                .padding(horizontal = 16.dp, vertical = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = attachmentItem.icon,
                                contentDescription = attachmentItem.title
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = attachmentItem.title
                            )
                        }
                        if (index != attachments.lastIndex) HorizontalDivider()
                    }
                }
            }
        }
    }
}

@ThemePreview
@Composable
private fun AttachmentsOverlayPreview() {
    KorenTheme {
        AttachmentsOverlay(
            uiState = ChatUiState.Shown(
                currentUserId = "1",
                chatItems = emptyList(),
                messageText = TextFieldValue("Lorem ipsum dolor sit amet"),
                showReactionPopup = false,
                targetMessageIdForReaction = null,
                shownTimestamps = emptySet(),
                attachmentsOverlayShown = true,
                profilePicsMap = emptyMap(),
                imageAttachments = emptySet(),
                sendingMessage = false,
                eventSink = {}
            ),
            imagePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = {}
            )
        )
    }
}