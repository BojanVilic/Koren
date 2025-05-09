package com.koren.chat.ui.chat

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue

data class MessageInputUiState(
    val messageText: TextFieldValue = TextFieldValue(""),
    val sendingMessage: Boolean = false,
    val imageAttachments: Set<Uri> = emptySet(),
    val attachmentsOverlayShown: Boolean = true
)
