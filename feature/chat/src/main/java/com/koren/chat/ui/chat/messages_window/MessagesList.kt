package com.koren.chat.ui.chat.messages_window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.designsystem.components.isEndReached
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
internal fun MessageList(
    modifier: Modifier = Modifier,
    uiState: MessagesWindowUiState.Shown
) {

    val endOfListReached by remember {
        derivedStateOf {
            uiState.listState.isEndReached()
        }
    }

    LaunchedEffect(endOfListReached, uiState.fetchingMore, uiState.canFetchMore, uiState.chatItems.isNotEmpty()) {
        if (endOfListReached && !uiState.fetchingMore && uiState.canFetchMore && uiState.chatItems.isNotEmpty()) {
            uiState.eventSink(MessagesWindowUiEvent.FetchMoreMessages)
        }
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
        state = uiState.listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        reverseLayout = true
    ) {
        itemsIndexed(
            key = { _, item -> item.id },
            items = uiState.chatItems
        ) { index, item ->
            when (item) {
                is ChatItem.DateSeparator -> {
                    val calendar = Calendar.getInstance().apply { timeInMillis = item.timestamp }
                    DateSeparator(calendar = calendar)
                }
                is ChatItem.MessageItem -> {
                    val message = item.message
                    val isPreviousMessageSameSender = when {
                        index < uiState.chatItems.size - 1 -> {
                            val prevItem = uiState.chatItems[index + 1]
                            prevItem is ChatItem.MessageItem &&
                                    prevItem.message.senderId == message.senderId
                        }
                        else -> false
                    }

                    MessageItem(
                        message = message,
                        isCurrentUser = message.senderId == uiState.currentUserId,
                        isPreviousMessageSameSender = isPreviousMessageSameSender,
                        onMessageClick = { uiState.eventSink(MessagesWindowUiEvent.OnMessageClicked(message.id)) },
                        onLongPress = { uiState.eventSink(MessagesWindowUiEvent.OpenMessageReactions(message.id)) },
                        timestampVisible = uiState.shownTimestamps.contains(message.id),
                        profilePic = uiState.profilePicsMap.getOrDefault(message.senderId, null),
                        onImageClicked = { uiState.eventSink(MessagesWindowUiEvent.OpenImageAttachment(it)) }
                    )
                }
            }
        }

        if (uiState.fetchingMore) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    text = stringResource(com.koren.designsystem.R.string.loading),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
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

@ThemePreview
@Composable
fun MessageListPreview() {
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
        MessageList(
            modifier = Modifier.fillMaxWidth(),
            uiState = MessagesWindowUiState.Shown(
                currentUserId = "user1",
                chatItems = chatItems,
                showReactionPopup = false,
                targetMessageIdForReaction = null,
                shownTimestamps = emptySet(),
                profilePicsMap = emptyMap(),
                eventSink = {}
            )
        )
    }
}