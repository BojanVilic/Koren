@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.designsystem.icon.Content
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import java.util.concurrent.TimeUnit

enum class MessageType {
    TEXT, IMAGE, VIDEO, VOICE
}

data class ChatMessage(
    val id: String? = null,
    val senderId: String = "",
    val senderName: String = "",
    val senderProfilePictureUrl: String? = null,
    val content: String = "",
    val mediaUrl: String? = null,
    val voiceDuration: Long? = null,
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val reactions: Map<String, String> = emptyMap() // Map of userId to emoji
)

@Composable
fun FamilyChatScreen(
    chatMessages: List<ChatMessage>,
    currentUserUid: String,
    familyName: String,
    onMessageSent: (String) -> Unit,
    onMediaMessageSent: (url: String, type: MessageType, caption: String?) -> Unit,
    onVoiceMessageSent: (url: String, duration: Long) -> Unit,
    onAttachmentClick: () -> Unit,
    onVoiceRecordClick: () -> Unit,
    onBackClick: () -> Unit,
    onReactToMessage: (messageId: String, emoji: String) -> Unit // New lambda for reactions
) {
    val listState = rememberLazyListState()
    var messageWithPickerVisible by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(familyName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            ChatComposeInput(
                onMessageSent = onMessageSent,
                onAttachmentClick = onAttachmentClick,
                onVoiceRecordClick = onVoiceRecordClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                reverseLayout = true
            ) {
                items(chatMessages.sortedByDescending { it.timestamp }, key = { it.id ?: it.timestamp }) { message ->
                    val isCurrentUser = message.senderId == currentUserUid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isCurrentUser) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(message.senderProfilePictureUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "${message.senderName}'s profile picture",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Bottom),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        ChatMessageItem(
                            message = message,
                            isCurrentUser = isCurrentUser,
                            showReactionPicker = messageWithPickerVisible == message.id,
                            onLongPress = { messageWithPickerVisible = if (messageWithPickerVisible == message.id) null else message.id },
                            onEmojiSelected = { emoji ->
                                message.id?.let { onReactToMessage(it, emoji) }
                                messageWithPickerVisible = null // Hide picker after selection
                            }
                        )

                        if (isCurrentUser) {
                            Spacer(modifier = Modifier.width(8.dp))
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(message.senderProfilePictureUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Your profile picture",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Bottom),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Dismiss overlay when reaction picker is visible
            if (messageWithPickerVisible != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { messageWithPickerVisible = null } // Dismiss on click
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isCurrentUser: Boolean,
    showReactionPicker: Boolean,
    onLongPress: () -> Unit,
    onEmojiSelected: (String) -> Unit
) {
    val bubbleShape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = if (isCurrentUser) 12.dp else 2.dp,
        bottomEnd = if (isCurrentUser) 2.dp else 12.dp
    )

    val bubbleColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val onBubbleColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    val reactionEmojis = remember { listOf("😂", "❤️", "👍", "😊", "🥳") }

    Column(
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
        modifier = Modifier
            .padding(
                start = if (!isCurrentUser) 0.dp else 48.dp,
                end = if (isCurrentUser) 0.dp else 48.dp
            )
    ) {
        if (!isCurrentUser) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Card(
            shape = bubbleShape,
            colors = CardDefaults.cardColors(
                containerColor = bubbleColor
            ),
            modifier = Modifier.combinedClickable(
                onClick = { /* Handle potential single click actions here */ },
                onLongClick = onLongPress
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyLarge,
                            color = onBubbleColor
                        )
                    }
                    MessageType.IMAGE -> {
                        message.mediaUrl?.let { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Image message",
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (message.content.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = onBubbleColor
                            )
                        }
                    }
                    MessageType.VIDEO -> {
                        message.mediaUrl?.let { url ->
                            Box(
                                modifier = Modifier
                                    .size(200.dp, 150.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { /* TODO: Play video */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play video",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (message.content.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = onBubbleColor
                            )
                        }
                    }
                    MessageType.VOICE -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play voice message",
                                modifier = Modifier.size(24.dp).clickable { /* TODO: Play voice */ },
                                tint = onBubbleColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatVoiceDuration(message.voiceDuration ?: 0L),
                                style = MaterialTheme.typography.bodyMedium,
                                color = onBubbleColor
                            )
                        }
                        if (message.content.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = onBubbleColor
                            )
                        }
                    }
                }
            }
        }

        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(if (isCurrentUser) Alignment.End else Alignment.Start)
                    .offset(y = (-4).dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.entries.groupBy { it.value }.forEach { (emoji, reactions) ->
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier
                            .height(24.dp)
                            .wrapContentWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = emoji,
                                style = MaterialTheme.typography.labelSmall
                            )
                            if (reactions.size > 1) {
                                Text(
                                    text = reactions.size.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showReactionPicker) {
            ReactionPicker(
                emojis = reactionEmojis,
                onEmojiSelected = onEmojiSelected,
                modifier = Modifier
                    .align(if (isCurrentUser) Alignment.End else Alignment.Start)
                    .offset(y = 4.dp)
            )
        }
    }
}

@Composable
fun ReactionPicker(
    emojis: List<String>,
    onEmojiSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emojis.forEach { emoji ->
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .clickable { onEmojiSelected(emoji) }
                        .padding(4.dp)
                )
            }
        }
    }
}


@Composable
fun ChatComposeInput(
    onMessageSent: (String) -> Unit,
    onAttachmentClick: () -> Unit = {},
    onVoiceRecordClick: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onAttachmentClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = KorenIcons.Content,
                    contentDescription = "Attach File",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp),
                placeholder = { Text("Type a message...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    disabledBorderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    errorContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                shape = CircleShape,
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                singleLine = false
            )

            FilledIconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onMessageSent(messageText.trim())
                        messageText = ""
                    } else {
                        onVoiceRecordClick()
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (messageText.isNotBlank()) Icons.Default.Send else Icons.Default.Call,
                    contentDescription = if (messageText.isNotBlank()) "Send Message" else "Record Voice Message"
                )
            }
        }
    }
}

fun formatVoiceDuration(durationMillis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
    return "%d:%02d".format(minutes, seconds)
}


@ThemePreview
@Composable
fun PreviewFamilyChatScreen() {
    KorenTheme {
        val sampleMessages = remember {
            listOf(
                ChatMessage(
                    id = "1",
                    senderId = "user1",
                    senderName = "Alice",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/FF0000",
                    content = "Hey family, check out this photo!",
                    type = MessageType.TEXT,
                    timestamp = System.currentTimeMillis() - 120000,
                    reactions = mapOf("currentUser" to "👍")
                ),
                ChatMessage(
                    id = "1a",
                    senderId = "user1",
                    senderName = "Alice",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/FF0000",
                    mediaUrl = "https://via.placeholder.com/400x300",
                    type = MessageType.IMAGE,
                    timestamp = System.currentTimeMillis() - 115000,
                    reactions = mapOf("currentUser" to "😂", "user2" to "😂", "user3" to "❤️")
                ),
                ChatMessage(
                    id = "2",
                    senderId = "currentUser",
                    senderName = "You",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/0000FF",
                    content = "Awesome photo!",
                    type = MessageType.TEXT,
                    timestamp = System.currentTimeMillis() - 110000,
                    reactions = emptyMap()
                ),
                ChatMessage(
                    id = "3",
                    senderId = "user2",
                    senderName = "Charlie",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/00FF00",
                    content = "Listen to this voice note:",
                    type = MessageType.TEXT,
                    timestamp = System.currentTimeMillis() - 100000,
                    reactions = emptyMap()
                ),
                ChatMessage(
                    id = "3a",
                    senderId = "user2",
                    senderName = "Charlie",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/00FF00",
                    mediaUrl = "voice_message_url",
                    voiceDuration = 15000,
                    type = MessageType.VOICE,
                    timestamp = System.currentTimeMillis() - 95000,
                    reactions = mapOf("currentUser" to "😊")
                ),
                ChatMessage(
                    id = "4",
                    senderId = "currentUser",
                    senderName = "You",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/0000FF",
                    content = "Got it!",
                    type = MessageType.TEXT,
                    timestamp = System.currentTimeMillis() - 90000,
                    reactions = emptyMap()
                ),
                ChatMessage(
                    id = "5",
                    senderId = "user1",
                    senderName = "Alice",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/FF0000",
                    content = "And here's a video!",
                    type = MessageType.TEXT,
                    timestamp = System.currentTimeMillis() - 80000,
                    reactions = emptyMap()
                ),
                ChatMessage(
                    id = "5a",
                    senderId = "user1",
                    senderName = "Alice",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/FF0000",
                    mediaUrl = "video_url",
                    type = MessageType.VIDEO,
                    timestamp = System.currentTimeMillis() - 75000,
                    reactions = emptyMap()
                ),
                ChatMessage(
                    id = "6",
                    senderId = "currentUser",
                    senderName = "You",
                    senderProfilePictureUrl = "https://via.placeholder.com/150/0000FF",
                    content = "This is the newest message.",
                    type = MessageType.TEXT,
                    timestamp = System.currentTimeMillis() - 5000,
                    reactions = emptyMap()
                )
            ).sortedBy { it.timestamp }
        }
        FamilyChatScreen(
            chatMessages = sampleMessages,
            currentUserUid = "currentUser",
            familyName = "The Family",
            onMessageSent = {},
            onMediaMessageSent = { _, _, _ -> },
            onVoiceMessageSent = { _, _ -> },
            onAttachmentClick = {},
            onVoiceRecordClick = {},
            onBackClick = {},
            onReactToMessage = { _, _ -> }
        )
    }
}

@ThemePreview
@Composable
fun PreviewChatComposeInputDark() {
    KorenTheme {
        ChatComposeInput(onMessageSent = {})
    }
}