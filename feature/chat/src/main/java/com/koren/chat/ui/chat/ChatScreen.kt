package com.koren.chat.ui.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.chat.ui.chat.message_input.AttachmentsOverlay
import com.koren.chat.ui.chat.message_input.MessageInputArea
import com.koren.chat.ui.chat.message_input.MessageInputUiEvent
import com.koren.chat.ui.chat.message_input.MessageInputUiState
import com.koren.chat.ui.chat.messages_window.MessageList
import com.koren.chat.ui.chat.messages_window.MessagesWindowUiState
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

@Serializable
object ChatDestination

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit,
    onNavigateToImageAttachment: (messageId: String) -> Unit,
    onNavigateToFullScreenImage: (mediaUrl: String) -> Unit,
    onNavigateToFullScreenVideo: (mediaUrl: String) -> Unit,
    onNavigateToMoreOptions: (messageId: String) -> Unit
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
            is ChatUiSideEffect.NavigateToImageAttachment -> onNavigateToImageAttachment(uiSideEffect.messageId)
            is ChatUiSideEffect.NavigateToFullScreenImage -> onNavigateToFullScreenImage(uiSideEffect.mediaUrl)
            is ChatUiSideEffect.NavigateToFullScreenVideo -> onNavigateToFullScreenVideo(uiSideEffect.videoUrl)
            is ChatUiSideEffect.NavigateToMoreOptions -> onNavigateToMoreOptions(uiSideEffect.messageId)
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
        is ChatUiState.Loading -> LoadingContent()
        is ChatUiState.Shown -> ChatScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun ChatScreenShownContent(
    uiState: ChatUiState.Shown
) {
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
        onResult = { uris ->
            uris.forEach { uri ->
                uiState.messageInputUiState.eventSink(MessageInputUiEvent.AddImageAttachment(uri))
            }
        }
    )

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val retriever = android.media.MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, uri)
                    val durationString = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val durationInSeconds = durationString?.toLongOrNull()?.milliseconds?.inWholeSeconds?: 0
                    uiState.messageInputUiState.eventSink(MessageInputUiEvent.AddVideoAttachment(uri, durationInSeconds))
                } catch (e: Exception) {
                    Timber.e("Error retrieving video duration: ${e.message}")
                } finally {
                    retriever.release()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        when (uiState.messagesWindowUiState) {
            is MessagesWindowUiState.Loading -> LoadingContent(modifier = Modifier.weight(1f))
            is MessagesWindowUiState.Shown -> MessageList(
                modifier = Modifier.weight(1f),
                uiState = uiState.messagesWindowUiState
            )
        }

        MessageInputArea(uiState = uiState.messageInputUiState)
    }

    AnimatedVisibility(
        modifier = Modifier.imePadding(),
        visible = uiState.messageInputUiState.attachmentsOverlayShown,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        AttachmentsOverlay(
            uiState = uiState.messageInputUiState,
            imagePicker = imagePicker,
            videoPicker = videoPicker
        )
    }
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
        ChatItem.MessageItem(ChatMessage("5", "user1", today - 10000, MessageType.TEXT, "Evo upravo. Ja vadim stvari iz auta.", null, null, null, mapOf("user2" to "👍"))),
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
                messagesWindowUiState = MessagesWindowUiState.Shown(
                    currentUserId = "user1",
                    chatItems = chatItems,
                    shownTimestamps = emptySet(),
                    messageSenderInfo = emptySet(),
                    eventSink = {}
                ),
                messageInputUiState = MessageInputUiState()
            )
        )
    }
}