package com.koren.chat.ui.chat.message_input

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.koren.chat.ui.chat.ChatUiSideEffect
import com.koren.common.models.chat.MessageType
import com.koren.data.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageInputPresenter @Inject constructor(
    private val chatRepository: ChatRepository,
    private val scope: CoroutineScope
) {

    @Composable
    fun present(
        sideEffects: MutableSharedFlow<ChatUiSideEffect>,
        listState: LazyListState,
    ): MessageInputUiState {

        var messageText by remember { mutableStateOf(TextFieldValue("")) }
        var sendingMessage by remember { mutableStateOf(false) }
        var imageAttachments by remember { mutableStateOf(emptySet<Uri>()) }
        var attachmentsOptionsOpen by remember { mutableStateOf(false) }
        var videoAttachment by remember { mutableStateOf<Uri?>(null) }
        val coroutineScope = rememberCoroutineScope()

        return MessageInputUiState(
            messageText = messageText,
            sendingMessage = sendingMessage,
            imageAttachments = imageAttachments,
            attachmentsOverlayShown = attachmentsOptionsOpen,
            videoAttachment = videoAttachment
        ) { event ->
            when (event) {
                is MessageInputUiEvent.OnMessageTextChanged -> messageText = event.text
                is MessageInputUiEvent.SendMessage -> {
                    scope.launch(Dispatchers.Default) {
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
                                coroutineScope.launch(Dispatchers.IO) {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            onFailure = { errorMessage ->
                                scope.launch {
                                    sideEffects.emit(ChatUiSideEffect.ShowError(errorMessage))
                                }
                                sendingMessage = false
                            }
                        )
                    }
                }
                is MessageInputUiEvent.AddImageAttachment -> {
                    imageAttachments = imageAttachments + event.imageUri
                    attachmentsOptionsOpen = false
                }
                is MessageInputUiEvent.RemoveImageAttachment -> imageAttachments = imageAttachments.minus(event.imageUri)
                is MessageInputUiEvent.ShowAttachmentsOverlay -> attachmentsOptionsOpen = true
                is MessageInputUiEvent.CloseAttachmentsOverlay -> attachmentsOptionsOpen = false
                is MessageInputUiEvent.AddVideoAttachment -> videoAttachment = event.videoUri
                is MessageInputUiEvent.RemoveVideoAttachment -> videoAttachment = null
            }
        }
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