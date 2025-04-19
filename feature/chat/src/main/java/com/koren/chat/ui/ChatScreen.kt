package com.koren.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldStateProvider = LocalScaffoldStateProvider.current

    scaffoldStateProvider.setScaffoldState(ScaffoldState(title = "Family Chat", isTopBarVisible = true, isBottomBarVisible = false))

    CollectSideEffects(viewModel = viewModel) { effect ->
        when (effect) {
            is ChatSideEffect.ShowErrorSnackbar -> onShowSnackbar(effect.message)
        }
    }

    ChatScreenContent(uiState = uiState)
}

@Composable
private fun ChatScreenContent(
    uiState: ChatUiState
) {
    when (uiState) {
        is ChatUiState.Loading -> ChatScreenLoading()
        is ChatUiState.Error -> ChatScreenError(message = uiState.message)
        is ChatUiState.Success -> ChatScreenSuccessContent(state = uiState)
        is ChatUiState.NoFamily -> ChatScreenNoFamily()
    }
}

@Composable
private fun ChatScreenSuccessContent(state: ChatUiState.Success) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(index = state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.messages.isEmpty()) {
                item { 
                    Text(
                        text = "No messages yet. Start the conversation!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                items(state.messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        isSentByCurrentUser = message.senderId == state.currentUser.id
                    )
                }
            }
        }
        ChatInput(
            text = state.messageInput,
            onTextChanged = { state.eventSink(ChatEvent.MessageInputChanged(it)) },
            onSendClick = { state.eventSink(ChatEvent.SendMessageClicked) },
            isSending = state.isSending,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
    }
}

@ThemePreview
@Composable
private fun ChatScreenSuccessContentPreview() {
    KorenTheme {
        Surface {
            ChatScreenSuccessContent(
                state = ChatUiState.Success(
                    messages = listOf(
                        ChatMessage(id = "1", senderId = "user1", senderName = "Alice", text = "Hello!", timestamp = System.currentTimeMillis() - 10000),
                        ChatMessage(id = "2", senderId = "currentUser", senderName = "You", text = "Hi Alice!", timestamp = System.currentTimeMillis() - 5000),
                        ChatMessage(id = "3", senderId = "user1", senderName = "Alice", text = "How are you?", timestamp = System.currentTimeMillis())
                    ),
                    currentUser = UserData(id = "user1"),
                    messageInput = "",
                    isSending = false,
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
private fun ChatScreenSuccessContentEmptyPreview() {
    KorenTheme {
        Surface {
            ChatScreenSuccessContent(
                state = ChatUiState.Success(
                    messages = emptyList(),
                    currentUser = UserData(id = "user1"),
                    messageInput = "",
                    isSending = false,
                    eventSink = {}
                )
            )
        }
    }
}

@Composable
private fun ChatScreenLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingContent()
    }
}

@Composable
private fun ChatScreenError(modifier: Modifier = Modifier, message: String) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Error: $message",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChatScreenNoFamily(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "You need to be part of a family to use the chat.",
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@ThemePreview
@Composable
private fun ChatScreenLoadingPreview() {
    KorenTheme {
        Surface {
            ChatScreenLoading()
        }
    }
}

@ThemePreview
@Composable
private fun ChatScreenErrorPreview() {
    KorenTheme {
        Surface {
            ChatScreenError(message = "Failed to load messages")
        }
    }
}

@ThemePreview
@Composable
private fun ChatScreenNoFamilyPreview() {
    KorenTheme {
        Surface {
            ChatScreenNoFamily()
        }
    }
}