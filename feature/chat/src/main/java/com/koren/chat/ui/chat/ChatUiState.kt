package com.koren.chat.ui.chat

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.input.TextFieldValue
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface ChatUiState : UiState {
    data object Loading : ChatUiState
    data class Shown(
        val currentUserId: String = "",
        val listState: LazyListState = LazyListState(),
        val chatItems: List<ChatItem> = emptyList(),
        val messageText: TextFieldValue = TextFieldValue(""),
        val showReactionPopup: Boolean = false,
        val targetMessageIdForReaction: String? = null,
        val shownTimestamps: Set<String> = emptySet(),
        val attachmentsOverlayShown: Boolean = true,
        val profilePicsMap: Map<String, String> = emptyMap(),
        val imageAttachments: Set<Uri> = emptySet(),
        val sendingMessage: Boolean = false,
        val fetchingMore: Boolean = false,
        val canFetchMore: Boolean = true,
        override val eventSink: (ChatUiEvent) -> Unit
    ) : ChatUiState, EventHandler<ChatUiEvent>
}

sealed interface ChatUiEvent : UiEvent {
    data object SendMessage : ChatUiEvent
    data class OnMessageTextChanged(val text: TextFieldValue) : ChatUiEvent
    data class OpenMessageReactions(val messageId: String) : ChatUiEvent
    data class OnReactionSelected(val messageId: String, val reaction: String) : ChatUiEvent
    data object DismissReactionPopup : ChatUiEvent
    data class OnMessageClicked(val messageId: String) : ChatUiEvent
    data object ShowAttachmentsOverlay : ChatUiEvent
    data object CloseAttachmentsOverlay : ChatUiEvent
    data class AddImageAttachment(val imageUri: Uri) : ChatUiEvent
    data class RemoveImageAttachment(val imageUri: Uri) : ChatUiEvent
    data object FetchMoreMessages : ChatUiEvent
    data class OpenImageAttachment(val message: ChatMessage) : ChatUiEvent
}

sealed interface ChatUiSideEffect : UiSideEffect {
    data class ShowError(val message: String) : ChatUiSideEffect
    data class NavigateToImageAttachment(val messageId: String) : ChatUiSideEffect
    data class NavigateToFullScreenImage(val mediaUrl: String) : ChatUiSideEffect
}