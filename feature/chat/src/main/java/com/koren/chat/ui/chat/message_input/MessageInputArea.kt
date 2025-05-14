package com.koren.chat.ui.chat.message_input

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.designsystem.icon.Close
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Video
import com.koren.designsystem.icon.Voice
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

@Composable
internal fun MessageInputArea(
    uiState: MessageInputUiState,
) {

    val showAttachmentsRow = uiState.imageAttachments.isNotEmpty() || uiState.videoAttachment != null

    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AnimatedVisibility(
                visible = showAttachmentsRow,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(200)
                ) + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 24.dp)
                        .padding(start = 16.dp, end = 64.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        Modifier.padding(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VideoAttachment(uiState = uiState)
                            ImageAttachments(uiState = uiState)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .clip(CircleShape)
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    value = uiState.messageText,
                    onValueChange = {
                        uiState.eventSink(MessageInputUiEvent.OnMessageTextChanged(it))
                    },
                    placeholder = { Text("Message...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    maxLines = 4,
                    leadingIcon = {
                        IconButton(onClick = { uiState.eventSink(MessageInputUiEvent.ShowAttachmentsOverlay) }) {
                            Icon(Icons.Default.Add, contentDescription = "Attach file")
                        }
                    },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                AnimatedSendMicButton(
                    isSendButton = uiState.messageText.text.isNotBlank() || uiState.imageAttachments.isNotEmpty(),
                    sendingMessage = uiState.sendingMessage,
                    onSendClick = { uiState.eventSink(MessageInputUiEvent.SendMessage) },
                    onMicClick = { uiState.eventSink(MessageInputUiEvent.ToggleVoiceRecorder) }
                )
            }

            AnimatedVisibility(uiState.voiceMessageMode) {
                VoiceRecorderAre(uiState = uiState)
            }
        }
    }
}

@Composable
private fun ImageAttachments(
    uiState: MessageInputUiState
) {
    uiState.imageAttachments.forEach { image ->
        Box(
            modifier = Modifier.size(84.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(image)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .offset {
                        IntOffset(0, -42)
                    }
                    .clip(CircleShape)
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        uiState.eventSink(
                            MessageInputUiEvent.RemoveImageAttachment(
                                image
                            )
                        )
                    },
                imageVector = KorenIcons.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun VideoAttachment(
    uiState: MessageInputUiState
) {

    if (uiState.videoAttachment != null) {
        Box(
            modifier = Modifier.size(84.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                model = uiState.videoThumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.4f)),
                error = ColorPainter(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.4f))
            )

            Box(
                modifier = Modifier
                    .padding(end = 21.dp, bottom = 21.dp)
                    .size(24.dp)
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = KorenIcons.Video,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .offset { IntOffset(0, -42) }
                    .clip(CircleShape)
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { uiState.eventSink(MessageInputUiEvent.RemoveVideoAttachment) },
                imageVector = KorenIcons.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AnimatedSendMicButton(
    isSendButton: Boolean,
    sendingMessage: Boolean,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit
) {

    val targetBackgroundColor =
        if (isSendButton) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceContainerHigh

    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(durationMillis = 200),
        label = "BackgroundColorAnimation"
    )

    val targetIconTintColor =
        if (isSendButton) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.primary

    val animatedIconTintColor by animateColorAsState(
        targetValue = targetIconTintColor,
        animationSpec = tween(durationMillis = 200),
        label = "IconTintColorAnimation"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(animatedBackgroundColor, CircleShape)
            .clickable(
                enabled = !sendingMessage,
                onClick = if (isSendButton) onSendClick else onMicClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (sendingMessage) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = if (isSendButton) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        } else {
            AnimatedContent(
                targetState = isSendButton,
                transitionSpec = {
                    scaleIn(animationSpec = tween(durationMillis = 300)) +
                            fadeIn(animationSpec = tween(durationMillis = 200)) togetherWith
                            scaleOut(animationSpec = tween(durationMillis = 300)) +
                            fadeOut(animationSpec = tween(durationMillis = 200)) using
                            SizeTransform(clip = false)
                },
                label = "IconAnimation",
                contentAlignment = Alignment.Center
            ) { targetIsSend ->
                Icon(
                    modifier = Modifier
                        .padding(6.dp),
                    imageVector = if (targetIsSend) Icons.AutoMirrored.Filled.Send else KorenIcons.Voice,
                    contentDescription = if (targetIsSend) "Send message" else "Record voice message",
                    tint = animatedIconTintColor
                )
            }
        }
    }
}

@ThemePreview
@Composable
private fun MessageInputAreaPreview() {
    KorenTheme {
        Surface {
            MessageInputArea(
                uiState = MessageInputUiState(
                    messageText = TextFieldValue(""),
                    sendingMessage = false,
                    imageAttachments = emptySet(),
                    attachmentsOverlayShown = false,
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
private fun MessageInputAreaSendingPreview() {
    KorenTheme {
        Surface {
            MessageInputArea(
                uiState = MessageInputUiState(
                    messageText = TextFieldValue(""),
                    sendingMessage = true,
                    imageAttachments = emptySet(),
                    attachmentsOverlayShown = false,
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
private fun MessageInputAreaWithAttachmentPreview() {
    KorenTheme {
        Surface {
            MessageInputArea(
                uiState = MessageInputUiState(
                    messageText = TextFieldValue(""),
                    sendingMessage = false,
                    imageAttachments = setOf(Uri.EMPTY),
                    attachmentsOverlayShown = false,
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
private fun MessageInputAreaWithTextPreview() {
    KorenTheme {
        Surface {
            MessageInputArea(
                uiState = MessageInputUiState(
                    messageText = TextFieldValue("Hello"),
                    sendingMessage = false,
                    imageAttachments = emptySet(),
                    attachmentsOverlayShown = false,
                    eventSink = {}
                )
            )
        }
    }
}
