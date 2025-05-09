package com.koren.chat.ui.chat

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.koren.common.models.chat.MessageType
import com.koren.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class MessageInputPresent @Inject constructor(
    private val chatRepository: ChatRepository
) {

    @Composable
    fun present(
        events: Flow<ChatUiEvent>,
        sideEffects: MutableSharedFlow<ChatUiSideEffect>,
        listState: LazyListState
    ): MessageInputUiState {

        val coroutineScope = rememberCoroutineScope()
        var messageText by remember { mutableStateOf(TextFieldValue("")) }
        var sendingMessage by remember { mutableStateOf(false) }
        var imageAttachments by remember { mutableStateOf(emptySet<Uri>()) }
        var attachmentsOptionsOpen by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is ChatUiEvent.OnMessageTextChanged -> messageText = event.text
                    is ChatUiEvent.SendMessage -> {
                        coroutineScope.launch(Dispatchers.Default) {
                            sendingMessage = true
                            sendMessage(
                                messageText = messageText.text,
                                messageType = getMessageType(
                                    messageText = messageText.text,
                                    imageAttachments = imageAttachments
                                ),
                                imageAttachments = imageAttachments,
                                onSuccess = {
                                    messageText = TextFieldValue("")
                                    imageAttachments = emptySet()
                                    sendingMessage = false
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                },
                                onFailure = { errorMessage ->
                                    coroutineScope.launch {
                                        sideEffects.emit(ChatUiSideEffect.ShowError(errorMessage))
                                    }
                                    sendingMessage = false
                                }
                            )
                        }
                    }
                    is ChatUiEvent.AddImageAttachment -> {
                        imageAttachments = imageAttachments + event.imageUri
                        attachmentsOptionsOpen = false
                    }
                    is ChatUiEvent.RemoveImageAttachment -> imageAttachments = imageAttachments.minus(event.imageUri)
                    is ChatUiEvent.ShowAttachmentsOverlay -> attachmentsOptionsOpen = true
                    is ChatUiEvent.CloseAttachmentsOverlay -> attachmentsOptionsOpen = false
                    else -> Unit
                }
            }
        }

        return MessageInputUiState(
            messageText = messageText,
            sendingMessage = sendingMessage,
            attachmentsOverlayShown = attachmentsOptionsOpen
        )
    }

    private suspend fun sendMessage(
        messageText: String,
        messageType: MessageType,
        imageAttachments: Set<Uri> = emptySet(),
        onSuccess: () -> Unit,
        onFailure: (errorMessage: String) -> Unit
    ) {
        when (messageType) {
            MessageType.TEXT -> chatRepository.sendTextMessage(messageText)
                .onSuccess { onSuccess() }
                .onFailure { onFailure("The message was not delivered. Please try again.") }
            MessageType.IMAGE -> chatRepository.sendImageMessage(imageAttachments, messageText)
                .onSuccess { onSuccess() }
                .onFailure { onFailure("The image was not delivered. Please try again.") }
            MessageType.VIDEO -> Unit
            MessageType.VOICE -> Unit
        }
    }

    private fun getMessageType(
        messageText: String,
        imageAttachments: Set<Uri>
    ): MessageType {
        return when {
            imageAttachments.isNotEmpty() -> MessageType.IMAGE
            messageText.isNotBlank() -> MessageType.TEXT
            else -> MessageType.TEXT
        }
    }
}