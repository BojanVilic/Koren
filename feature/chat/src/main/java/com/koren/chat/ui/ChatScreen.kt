@file:OptIn(ExperimentalFoundationApi::class)

package com.koren.chat.ui

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.chat.ui.model.AttachmentOptions
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.Scrim
import com.koren.designsystem.icon.Close
import com.koren.designsystem.icon.Files
import com.koren.designsystem.icon.Image
import com.koren.designsystem.icon.ImageStack
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Video
import com.koren.designsystem.icon.Voice
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Serializable
object ChatDestination

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isBottomBarVisible = false
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is ChatUiSideEffect.ShowError -> onShowSnackbar(uiSideEffect.message)
        }
    }

    ChatScreenContent(
        uiState = uiState
    )
}

@Composable
private fun ChatScreenContent(
    uiState: ChatUiState
) {
    when (uiState) {
        is ChatUiState.Loading -> CircularProgressIndicator()
        is ChatUiState.Shown -> ChatScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun ChatScreenShownContent(
    uiState: ChatUiState.Shown
) {
    val listState = rememberLazyListState()
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                uiState.eventSink(ChatUiEvent.AddImageAttachment(it))
            }
        }
    )

    LaunchedEffect(uiState.chatItems.size) {
        if (uiState.chatItems.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        MessageList(
            modifier = Modifier.weight(1f),
            chatItems = uiState.chatItems,
            currentUserId = uiState.currentUserId,
            onMessageClick = { messageId ->
                uiState.eventSink(ChatUiEvent.OnMessageClicked(messageId))
            },
            onMessageLongPress = { messageId ->
                uiState.eventSink(ChatUiEvent.OpenMessageReactions(messageId))
            },
            listState = listState,
            shownTimestamps = uiState.shownTimestamps,
            profilePicsMap = uiState.profilePicsMap
        )

        MessageInputArea(
            text = uiState.messageText,
            onTextChange = { uiState.eventSink(ChatUiEvent.OnMessageTextChanged(it)) },
            sendingMessage = uiState.sendingMessage,
            onSendClick = {
                uiState.eventSink(ChatUiEvent.SendMessage)
            },
            onAttachmentClick = { uiState.eventSink(ChatUiEvent.ShowAttachmentsOverlay) },
            onMicClick = { },
            imageAttachments = uiState.imageAttachments,
            onRemoveImageAttachment = { uri ->
                uiState.eventSink(ChatUiEvent.RemoveImageAttachment(uri))
            }
        )

        if (uiState.showReactionPopup && uiState.targetMessageIdForReaction != null) {
            ReactionSelectionDialog(
                onDismissRequest = { uiState.eventSink(ChatUiEvent.DismissReactionPopup) },
                onReactionSelected = { reaction ->
                    uiState.targetMessageIdForReaction.let { msgId ->
                        uiState.eventSink(ChatUiEvent.OnReactionSelected(msgId, reaction))
                    }
                    uiState.eventSink(ChatUiEvent.DismissReactionPopup)
                }
            )
        }
    }

    AnimatedVisibility(
        modifier = Modifier.imePadding(),
        visible = uiState.attachmentsOverlayShown,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        AttachmentsOverlay(
            uiState = uiState,
            imagePicker = imagePicker
        )
    }
}

@Composable
private fun AttachmentsOverlay(
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

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    chatItems: List<ChatItem>,
    currentUserId: String,
    listState: LazyListState = rememberLazyListState(),
    onMessageClick: (messageId: String) -> Unit,
    onMessageLongPress: (messageId: String) -> Unit,
    shownTimestamps: Set<String>,
    profilePicsMap: Map<String, String>
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        reverseLayout = true
    ) {
        itemsIndexed(
            key = { _, item -> item.id },
            items = chatItems
        ) { index, item ->
            when (item) {
                is ChatItem.DateSeparator -> {
                    val calendar = Calendar.getInstance().apply { timeInMillis = item.timestamp }
                    DateSeparator(calendar = calendar)
                }
                is ChatItem.MessageItem -> {
                    val message = item.message
                    val isPreviousMessageSameSender = when {
                        index < chatItems.size - 1 -> {
                            val prevItem = chatItems[index + 1]
                            prevItem is ChatItem.MessageItem &&
                                    prevItem.message.senderId == message.senderId
                        }
                        else -> false
                    }

                    MessageItem(
                        message = message,
                        isCurrentUser = message.senderId == currentUserId,
                        isPreviousMessageSameSender = isPreviousMessageSameSender,
                        onMessageClick = { onMessageClick(message.id) },
                        onLongPress = { onMessageLongPress(message.id) },
                        timestampVisible = shownTimestamps.contains(message.id),
                        profilePic = profilePicsMap.getOrDefault(message.senderId, null)
                    )
                }
            }
        }
    }
}

@Composable
fun DateSeparator(calendar: Calendar) {
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val dateText = when {
        isSameDay(calendar, today) -> "Today"
        isSameDay(calendar, yesterday) -> "Yesterday"
        else -> SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(calendar.time)
    }
    val timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            text = "$dateText • $timeText",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun MessageItem(
    message: ChatMessage,
    isCurrentUser: Boolean,
    isPreviousMessageSameSender: Boolean,
    onMessageClick: (String) -> Unit,
    onLongPress: () -> Unit,
    timestampVisible: Boolean,
    profilePic: String? = null
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCurrentUser.not()) {
            if (isPreviousMessageSameSender.not()) {
                AsyncImage(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .clip(CircleShape)
                        .size(36.dp)
                        .align(Alignment.Top),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profilePic)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Sent image",
                    contentScale = ContentScale.Crop
                )
            } else {
                Spacer(modifier = Modifier.width(44.dp))
            }
        }

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
                            onClick = { onMessageClick(message.id) },
                            onLongClick = onLongPress
                        ),
                    shape = MaterialTheme.shapes.medium,
                    color = backgroundColor
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        when (message.messageType) {
                            MessageType.TEXT -> {
                                message.textContent?.let {
                                    Text(
                                        text = it,
                                        color = textColor,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            MessageType.IMAGE -> {
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
                                                .clip(RoundedCornerShape(8.dp)),
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(mediaUrls.first())
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Sent image",
                                            contentScale = ContentScale.Crop
                                        )
                                        if (mediaUrls.size > 1) {
                                            Row(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(top = 4.dp, end = 4.dp)
                                                    .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.medium)
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

                            MessageType.VIDEO -> {
                                Box(contentAlignment = Alignment.Center) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .heightIn(max = 200.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        model = message.mediaUrls,
                                        contentDescription = "Sent video",
                                        contentScale = ContentScale.Fit
                                    )
                                    Icon(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                Color.Black.copy(alpha = 0.4f),
                                                RoundedCornerShape(50)
                                            ),
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play video",
                                        tint = Color.White
                                    )
                                }
                            }

                            MessageType.VOICE -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // TODO: Implement actual voice player UI
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Voice message",
                                        tint = textColor
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Voice Message",
                                        color = textColor,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    message.mediaDuration?.let {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = formatDuration(it),
                                            color = textColor.copy(alpha = 0.7f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.align(reactionAlignment),
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
    }
}

fun formatDuration(seconds: Long): String {
    val minutes = TimeUnit.SECONDS.toMinutes(seconds)
    val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Composable
fun MessageInputArea(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    sendingMessage: Boolean,
    onSendClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    onMicClick: () -> Unit,
    imageAttachments: Set<Uri>,
    onRemoveImageAttachment: (Uri) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AnimatedVisibility(
                visible = imageAttachments.isNotEmpty(),
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
                            imageAttachments.forEach { image ->
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
                                                onRemoveImageAttachment(image)
                                            },
                                        imageVector = KorenIcons.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
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
                    value = text,
                    onValueChange = onTextChange,
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
                        IconButton(onClick = onAttachmentClick) {
                            Icon(Icons.Default.Add, contentDescription = "Attach file")
                        }
                    },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                AnimatedSendMicButton(
                    isSendButton = text.text.isNotBlank() || imageAttachments.isNotEmpty(),
                    sendingMessage = sendingMessage,
                    onSendClick = onSendClick,
                    onMicClick = onMicClick
                )
            }
        }
    }
}

@Composable
fun AnimatedSendMicButton(
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

@Composable
fun ReactionSelectionDialog(
    onDismissRequest: () -> Unit,
    onReactionSelected: (String) -> Unit
) {
    val reactions = listOf("👍", "❤️", "😂", "😮", "😢", "🙏")

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("React to message") },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                reactions.forEach { reaction ->
                    TextButton(onClick = { onReactionSelected(reaction) }) {
                        Text(reaction, fontSize = 24.sp)
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@ThemePreview
@Composable
fun ChatScreenPreview() {
    val dayBeforeYesterday = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)
    val yesterday = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
    val today = System.currentTimeMillis()

    val chatItems = listOf(
        ChatItem.DateSeparator(today),
        ChatItem.MessageItem(ChatMessage("7", "user2", today - 2000, MessageType.VOICE, null, null, 45L, null)),
        ChatItem.MessageItem(ChatMessage("6", "user1", today - 5000, MessageType.IMAGE, "Image Message", listOf("https://upload.wikimedia.org/wikipedia/commons/thumb/6/64/Android_logo_2019_%28stacked%29.svg/2346px-Android_logo_2019_%28stacked%29.svg.png"), null, null)),
        ChatItem.MessageItem(ChatMessage("5", "user1", today - 10000, MessageType.TEXT, "Evo upravo. Ja vadim stvari iz auta.", null, null, mapOf("user2" to "👍"))),
        ChatItem.DateSeparator(yesterday),
        ChatItem.MessageItem(ChatMessage("4b", "user2", yesterday - 50000, MessageType.TEXT, "Jeste stigli", null, null, null)),
        ChatItem.MessageItem(ChatMessage("4", "user2", yesterday - 60000, MessageType.TEXT, "Jeste stigli", null, null, null)),
        ChatItem.DateSeparator(dayBeforeYesterday),
        ChatItem.MessageItem(ChatMessage("3b", "user2", dayBeforeYesterday - 70000, MessageType.TEXT, "Pita vanja jel idete u kostariku", null, null, null)),
        ChatItem.MessageItem(ChatMessage("3", "user2", dayBeforeYesterday - 80000, MessageType.TEXT, "Vreme vam je da pocnete farbati, vise niste sami", null, null, null)),
        ChatItem.MessageItem(ChatMessage("2", "user1", dayBeforeYesterday - 90000, MessageType.TEXT, "Ma kaki.", null, null, null)),
        ChatItem.MessageItem(ChatMessage("1", "user2", dayBeforeYesterday - 100000, MessageType.TEXT, "Jeste kupili boje za farbanje jaja", null, null, null))
    )

    KorenTheme {
        ChatScreenContent(
            uiState = ChatUiState.Shown(
                currentUserId = "user1",
                chatItems = chatItems,
                messageText = TextFieldValue(""),
                showReactionPopup = false,
                attachmentsOverlayShown = false,
                profilePicsMap = mapOf(
                    "user2" to "https://i.pravatar.cc/150?img=2"
                ),
                eventSink = {}
            )
        )
    }
}