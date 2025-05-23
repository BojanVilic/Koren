package com.koren.chat.ui.chat.messages_window

import androidx.compose.foundation.lazy.LazyListState
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState

sealed interface MessagesWindowUiState : UiState {
    data object Loading : MessagesWindowUiState
    data class Shown(
        val currentUserId: String = "",
        val listState: LazyListState = LazyListState(),
        val chatItems: List<ChatItem> = emptyList(),
        val showReactionPopup: Boolean = false,
        val targetMessageIdForReaction: String? = null,
        val shownTimestamps: Set<String> = emptySet(),
        val profilePicsMap: Map<String, String> = emptyMap(),
        val fetchingMore: Boolean = false,
        val canFetchMore: Boolean = true,
        override val eventSink: (MessagesWindowUiEvent) -> Unit
    ) : MessagesWindowUiState, EventHandler<MessagesWindowUiEvent>
}

sealed interface MessagesWindowUiEvent : UiEvent {
    data class OpenMessageReactions(val messageId: String) : MessagesWindowUiEvent
    data class OnReactionSelected(val messageId: String, val reaction: String) : MessagesWindowUiEvent
    data object DismissReactionPopup : MessagesWindowUiEvent
    data class OnMessageClicked(val messageId: String) : MessagesWindowUiEvent
    data object FetchMoreMessages : MessagesWindowUiEvent
    data class OpenImageAttachment(val message: ChatMessage) : MessagesWindowUiEvent
    data class OpenVideoAttachment(val message: ChatMessage) : MessagesWindowUiEvent
}