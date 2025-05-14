package com.koren.chat.ui.chat.message_input

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState

data class MessageInputUiState(
    val messageText: TextFieldValue = TextFieldValue(""),
    val sendingMessage: Boolean = false,
    val imageAttachments: Set<Uri> = emptySet(),
    val attachmentsOverlayShown: Boolean = true,
    val videoAttachment: Uri? = null,
    val videoDuration: Long = 0L,
    val videoThumbnail: Bitmap? = null,
    val voiceMessageMode: Boolean = false,
    val voiceMessageRecording: Boolean = false,
    val voiceMessageDuration: Long = 0L,
    override val eventSink: (MessageInputUiEvent) -> Unit = {}
) : UiState, EventHandler<MessageInputUiEvent>

sealed interface MessageInputUiEvent : UiEvent {
    data object SendMessage : MessageInputUiEvent
    data class OnMessageTextChanged(val text: TextFieldValue) : MessageInputUiEvent
    data class AddImageAttachment(val imageUri: Uri) : MessageInputUiEvent
    data class RemoveImageAttachment(val imageUri: Uri) : MessageInputUiEvent
    data class AddVideoAttachment(val videoUri: Uri, val duration: Long) : MessageInputUiEvent
    data object RemoveVideoAttachment : MessageInputUiEvent
    data object ShowAttachmentsOverlay : MessageInputUiEvent
    data object CloseAttachmentsOverlay : MessageInputUiEvent
    data object ToggleVoiceRecorder : MessageInputUiEvent
    data object StartRecording : MessageInputUiEvent
}