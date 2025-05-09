package com.koren.chat.ui.attachments_list

import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiSideEffect
import com.koren.common.util.UiState

sealed interface AttachmentsListUiState : UiState {
    data object Loading : AttachmentsListUiState
    data class Shown(
        val mediaUrls: List<String> = emptyList(),
        override val eventSink: (AttachmentsListUiEvent) -> Unit
    ) : AttachmentsListUiState, EventHandler<AttachmentsListUiEvent>
}

sealed interface AttachmentsListUiEvent : UiEvent {
    data class OnImageClick(val imageUrl: String) : AttachmentsListUiEvent

}

sealed interface AttachmentsListUiSideEffect : UiSideEffect {
    data class ShowError(val message: String) : AttachmentsListUiSideEffect
    data class NavigateToFullScreenImage(val mediaUrl: String) : AttachmentsListUiSideEffect
}