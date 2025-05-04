package com.koren.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.util.MoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
): MoleculeViewModel<ChatUiEvent, ChatUiState, ChatUiSideEffect>() {

    override fun setInitialState(): ChatUiState = ChatUiState.Loading

    @Composable
    override fun produceState(): ChatUiState {
        val sampleMessages = listOf(
            ChatMessage("1", "user2", System.currentTimeMillis() - 100000000, MessageType.TEXT, "Jeste kupili boje za farbanje jaja", null, null, null),
            ChatMessage("2", CURRENT_USER_ID, System.currentTimeMillis() - 90000000, MessageType.TEXT, "Ma kaki.", null, null, null),
            ChatMessage("3", "user2", System.currentTimeMillis() - 80000000, MessageType.TEXT, "Vreme vam je da pocnete farbati, vise niste sami", null, null, null),
            ChatMessage("3b", "user2", System.currentTimeMillis() - 70000000, MessageType.TEXT, "Pita vanja jel idete u kostariku", null, null, null),
            ChatMessage("4", "user2", System.currentTimeMillis() - 50000, MessageType.TEXT, "Jeste stigli", null, null, null),
            ChatMessage("5", CURRENT_USER_ID, System.currentTimeMillis() - 10000, MessageType.TEXT, "Evo upravo. Ja vadim stvari iz auta.", null, null, mapOf("user2" to "👍")),
            ChatMessage("6", CURRENT_USER_ID, System.currentTimeMillis() - 5000, MessageType.IMAGE, "Image Message", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/64/Android_logo_2019_%28stacked%29.svg/2346px-Android_logo_2019_%28stacked%29.svg.png", null, null),
            ChatMessage("7", "user2", System.currentTimeMillis() - 2000, MessageType.VOICE, null, null, 45L, null),
        )

        var messageText by remember { mutableStateOf("") }
        var showReactionPopup by remember { mutableStateOf(false) }
        var targetMessageIdForReaction by remember { mutableStateOf<String?>(null) }

        return ChatUiState.Shown(
            messages = sampleMessages,
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
                is ChatUiEvent.SendMessage -> Unit
                is ChatUiEvent.OnReactionSelected -> Unit
            }
        }
    }
}