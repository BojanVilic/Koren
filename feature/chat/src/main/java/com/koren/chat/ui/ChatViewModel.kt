package com.koren.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.koren.common.models.chat.MessageType
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userSession: UserSession
): MoleculeViewModel<ChatUiEvent, ChatUiState, ChatUiSideEffect>() {

    override fun setInitialState(): ChatUiState = ChatUiState.Loading

    @Composable
    override fun produceState(): ChatUiState {
        val messages by chatRepository.getChatMessages().collectAsState(initial = emptyList())
        val currentUserId by userSession.currentUser.map { it.id }.collectAsState(initial = "")

        var messageText by remember { mutableStateOf("") }
        var showReactionPopup by remember { mutableStateOf(false) }
        var targetMessageIdForReaction by remember { mutableStateOf<String?>(null) }

        return ChatUiState.Shown(
            currentUserId = currentUserId,
            messages = messages,
            messageText = messageText,
            showReactionPopup = showReactionPopup,
            targetMessageIdForReaction = targetMessageIdForReaction
        ) { event ->
            when (event) {
                is ChatUiEvent.DismissReactionPopup -> {
                    showReactionPopup = false
                    targetMessageIdForReaction = null
                }
                is ChatUiEvent.OnMessageTextChanged -> messageText = event.text
                is ChatUiEvent.OpenMessageReactions -> {
                    showReactionPopup = true
                    targetMessageIdForReaction = event.messageId
                }
                is ChatUiEvent.SendMessage -> sendMessage(messageText, MessageType.TEXT)
                is ChatUiEvent.OnReactionSelected -> Unit
            }
        }
    }

    private fun sendMessage(
        messageText: String,
        messageType: MessageType
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            when (messageType) {
                MessageType.TEXT -> chatRepository.sendTextMessage(messageText)
                MessageType.IMAGE -> Unit
                MessageType.VIDEO -> Unit
                MessageType.VOICE -> Unit
            }
        }
    }
}