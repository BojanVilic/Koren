package com.koren.chat.ui.chat

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.koren.chat.ui.chat.message_input.MessageInputPresenter
import com.koren.chat.ui.chat.messages_window.MessagesWindowPresenter
import com.koren.common.util.MoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageInputPresenter: MessageInputPresenter,
    private val messagesWindowPresenter: MessagesWindowPresenter
): MoleculeViewModel<ChatUiEvent, ChatUiState, ChatUiSideEffect>() {

    override fun setInitialState(): ChatUiState = ChatUiState.Loading

    @Composable
    override fun produceState(): ChatUiState {
        val listState = rememberLazyListState()
        return ChatUiState.Shown(
            messagesWindowUiState = messagesWindowPresenter.present(_sideEffects, listState),
            messageInputUiState = messageInputPresenter.present(_sideEffects, listState),
        )
    }
}