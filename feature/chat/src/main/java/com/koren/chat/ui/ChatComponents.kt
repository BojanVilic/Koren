@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.koren.common.models.chat.ChatMessage // Use the common model

/**
 * Displays a single chat message bubble.
 */
@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    message: ChatMessage,
    isSentByCurrentUser: Boolean
) {
    val alignment = if (isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = if (isSentByCurrentUser) 12.dp else 2.dp,
        bottomEnd = if (isSentByCurrentUser) 2.dp else 12.dp
    )
    val backgroundColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isSentByCurrentUser) 48.dp else 8.dp,
                end = if (isSentByCurrentUser) 8.dp else 48.dp,
                top = 2.dp,
                bottom = 2.dp
            ),
        contentAlignment = alignment
    ) {
        Surface(
            shape = bubbleShape,
            color = backgroundColor,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            // TODO: Add sender name/timestamp if needed, handle other message types later
        }
    }
}

/**
 * Input field for composing and sending chat messages.
 */
@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences
            ),
            enabled = enabled && !isSending, // Disable when sending
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            maxLines = 5
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSendClick,
            enabled = enabled && text.isNotBlank() && !isSending,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (enabled && text.isNotBlank() && !isSending) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send message",
                    tint = if (enabled && text.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}

// Removed FamilyChatScreen, ChatMessageItem (renamed to MessageBubble), ChatComposeInput (renamed to ChatInput), EmojiReactionPicker,
// local ChatMessage data class, and MessageType enum.