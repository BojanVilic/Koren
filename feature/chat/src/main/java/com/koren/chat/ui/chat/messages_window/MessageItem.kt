package com.koren.chat.ui.chat.messages_window

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.chat.ui.chat.message_input.voice_message.PlaybackState
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.util.DateUtils.formatDuration
import com.koren.designsystem.components.InitialsAvatar
import com.koren.designsystem.icon.ImageStack
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Pause
import com.koren.designsystem.icon.Play
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun MessageItem(
    modifier: Modifier = Modifier,
    message: ChatMessage,
    isCurrentUser: Boolean,
    isPreviousMessageSameSender: Boolean,
    uiState: MessagesWindowUiState.Shown
) {
    val arrangement =
        if (isCurrentUser) Arrangement.End
        else Arrangement.Start

    val reactionAlignment =
        if (isCurrentUser) Alignment.End
        else Alignment.Start

    val backgroundColor =
        if (isCurrentUser) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceContainerHigh

    val textColor =
        if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SenderProfileImage(
            isCurrentUser = isCurrentUser,
            isPreviousMessageSameSender = isPreviousMessageSameSender,
            messageSenderInfo = uiState.messageSenderInfo.firstOrNull { it.id == message.senderId }
        )
        MessageBubble(
            reactionAlignment = reactionAlignment,
            message = message,
            backgroundColor = backgroundColor,
            textColor = textColor,
            uiState = uiState
        )
    }
}

@Composable
private fun MessageBubble(
    reactionAlignment: Alignment.Horizontal,
    backgroundColor: Color,
    textColor: Color,
    message: ChatMessage,
    uiState: MessagesWindowUiState.Shown
) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Surface(
                modifier = Modifier
                    .align(reactionAlignment)
                    .clip(MaterialTheme.shapes.small)
                    .combinedClickable(
                        onClick = { uiState.eventSink(MessagesWindowUiEvent.OnMessageClicked(message.id)) },
                        onLongClick = {
                            uiState.eventSink(
                                MessagesWindowUiEvent.OpenMoreOptions(
                                    message.id
                                )
                            )
                        }
                    ),
                shape = MaterialTheme.shapes.medium,
                color = backgroundColor
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    when (message.messageType) {
                        MessageType.TEXT -> TextMessage(message.textContent, textColor)
                        MessageType.IMAGE -> ImageMessage(
                            message = message,
                            textColor = textColor,
                            onClick = {
                                uiState.eventSink(MessagesWindowUiEvent.OpenImageAttachment(it))
                            }
                        )
                        MessageType.VIDEO -> VideoMessage(
                            message = message,
                            onVideoClick = {
                                uiState.eventSink(MessagesWindowUiEvent.OpenImageAttachment(it))
                            }
                        )
                        MessageType.VOICE -> VoiceMessage(
                            message = message,
                            uiState = uiState
                        )
                    }
                }
            }
        }

        ReactionsAndTimestamp(
            reactionAlignment = reactionAlignment,
            message = message,
            timestampVisible = uiState.shownTimestamps.contains(message.id)
        )
    }
}

@Composable
private fun TextMessage(
    text: String?,
    textColor: Color
) {
    text?.let {
        Text(
            text = it,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ImageMessage(
    message: ChatMessage,
    textColor: Color,
    onClick: (message: ChatMessage) -> Unit
) {
    val mediaUrls = message.mediaUrls
    if (!mediaUrls.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .heightIn(max = 200.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onClick(message)
                    },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mediaUrls.first())
                    .crossfade(true)
                    .build(),
                contentDescription = "Sent image",
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.4f))
            )
            if (mediaUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            MaterialTheme.shapes.medium
                        )
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mediaUrls.size.toString(),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = KorenIcons.ImageStack,
                        contentDescription = "Multiple images",
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )
                }
            }
        }
    }

    message.textContent?.takeIf { it.isNotBlank() }?.let {
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = it,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun VideoMessage(
    message: ChatMessage,
    onVideoClick: (message: ChatMessage) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        AsyncImage(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    onVideoClick(message)
                },
            model = message.mediaUrls?.first(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.4f)),
            error = ColorPainter(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.4f))
        )
        Icon(
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color.Black.copy(alpha = 0.4f),
                    RoundedCornerShape(50)
                )
                .clickable {
                    onVideoClick(message)
                },
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play video",
            tint = Color.White
        )
    }
}

@Composable
private fun VoiceMessage(
    message: ChatMessage,
    uiState: MessagesWindowUiState.Shown
) {
    Row(
        modifier = Modifier
            .height(64.dp)
            .widthIn(max = 224.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                when (uiState.playbackState) {
                    PlaybackState.PLAYING -> {
                        uiState.eventSink(MessagesWindowUiEvent.PausePlayback)
                        if (message.id != uiState.currentlyPlayingMessageId)
                            uiState.eventSink(MessagesWindowUiEvent.StartPlayback(message))
                    }
                    PlaybackState.PAUSED ->
                        if (message.id == uiState.currentlyPlayingMessageId) uiState.eventSink(MessagesWindowUiEvent.ResumePlayback)
                        else uiState.eventSink(MessagesWindowUiEvent.StartPlayback(message))
                    PlaybackState.STOPPED -> uiState.eventSink(MessagesWindowUiEvent.StartPlayback(message))
                }
            }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = if (message.id == uiState.currentlyPlayingMessageId && uiState.playbackState == PlaybackState.PLAYING) KorenIcons.Pause else KorenIcons.Play,
                contentDescription = "Play voice message",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Slider(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            value = if (message.id == uiState.currentlyPlayingMessageId) uiState.playbackPosition else 0f,
            onValueChange = { progress ->
                uiState.eventSink(MessagesWindowUiEvent.SeekVoiceMessageTo(progress))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onSurface,
                activeTrackColor = MaterialTheme.colorScheme.onSurface,
                inactiveTrackColor = Color.Gray
            )
        )

        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = formatDuration(message.mediaDuration?: 0L),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ColumnScope.ReactionsAndTimestamp(
    reactionAlignment: Alignment.Horizontal,
    message: ChatMessage,
    timestampVisible: Boolean
) {
    Row(
        modifier = Modifier.Companion.align(reactionAlignment),
        verticalAlignment = Alignment.CenterVertically
    ) {
        message.reactions?.takeIf { it.isNotEmpty() }?.let { reactions ->
            Text(
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.dp, MaterialTheme.colorScheme.background, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = reactions.values.firstOrNull() ?: " ",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (timestampVisible) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(Date(message.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RowScope.SenderProfileImage(
    isCurrentUser: Boolean,
    isPreviousMessageSameSender: Boolean,
    messageSenderInfo: MessageSenderInfo?
) {
    if (isCurrentUser.not()) {
        if (isPreviousMessageSameSender.not()) {
            Column(
                modifier = Modifier.align(Alignment.Top)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                InitialsAvatar(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(36.dp),
                    imageUrl = messageSenderInfo?.profilePictureUrl,
                    name = messageSenderInfo?.name ?: ""
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

@ThemePreview
@Composable
private fun PreviewTextMessage() {
    KorenTheme {
        MessageItem(
            message = ChatMessage(
                id = "1",
                senderId = "user2",
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.TEXT,
                textContent = "This is a text message."
            ),
            isCurrentUser = false,
            isPreviousMessageSameSender = false,
            uiState = MessagesWindowUiState.Shown(
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
private fun PreviewImageMessage() {
    KorenTheme {
        MessageItem(
            message = ChatMessage(
                id = "2",
                senderId = "user1",
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.IMAGE,
                mediaUrls = listOf("https://i.ytimg.com/vi/psJJcrni2Jg/maxresdefault.jpg"),
                textContent = "Check out this image!"
            ),
            isCurrentUser = true,
            isPreviousMessageSameSender = false,
            uiState = MessagesWindowUiState.Shown(
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
private fun PreviewVideoMessage() {
    KorenTheme {
        MessageItem(
            message = ChatMessage(
                id = "3",
                senderId = "user2",
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.VIDEO,
                mediaUrls = listOf("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4")
            ),
            isCurrentUser = false,
            isPreviousMessageSameSender = false,
            uiState = MessagesWindowUiState.Shown(
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
private fun PreviewVoiceMessage() {
    KorenTheme {
        MessageItem(
            message = ChatMessage(
                id = "4",
                senderId = "user1",
                timestamp = System.currentTimeMillis(),
                messageType = MessageType.VOICE,
                mediaDuration = 45L
            ),
            isCurrentUser = true,
            isPreviousMessageSameSender = false,
            uiState = MessagesWindowUiState.Shown(
                eventSink = {}
            )
        )
    }
}