package com.koren.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.koren.common.models.chat.MessageType
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ChatRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import com.koren.domain.GetAllFamilyMembersWithDetailsUseCase
import com.koren.domain.GetFamilyMemberUseCase
import com.koren.domain.GetFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userSession: UserSession,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase
): MoleculeViewModel<ChatUiEvent, ChatUiState, ChatUiSideEffect>() {

    override fun setInitialState(): ChatUiState = ChatUiState.Loading

    @Composable
    override fun produceState(): ChatUiState {
        val messages by chatRepository.getChatMessages().collectAsState(initial = emptyList())
        val currentUserId by userSession.currentUser.map { it.id }.collectAsState(initial = "")
        val familyMembers by getAllFamilyMembersUseCase.invoke().collectAsState(initial = emptyList())

        var messageText by remember { mutableStateOf(TextFieldValue("")) }
        var showReactionPopup by remember { mutableStateOf(false) }
        var targetMessageIdForReaction by remember { mutableStateOf<String?>(null) }
        var shownTimestamps by remember { mutableStateOf(emptySet<String>()) }
        var attachmentsOptionsOpen by remember { mutableStateOf(false) }
        val profilePicsMap by remember {
            derivedStateOf {
                familyMembers.associate { member ->
                    member.id to member.profilePictureUrl
                }
            }
        }

        return ChatUiState.Shown(
            currentUserId = currentUserId,
            messages = messages,
            messageText = messageText,
            showReactionPopup = showReactionPopup,
            targetMessageIdForReaction = targetMessageIdForReaction,
            shownTimestamps = shownTimestamps,
            attachmentsOverlayShown = attachmentsOptionsOpen,
            profilePicsMap = profilePicsMap
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
                is ChatUiEvent.SendMessage -> sendMessage(
                    messageText = messageText.text,
                    messageType = MessageType.TEXT,
                    onSuccess = { messageText = TextFieldValue("") }
                )
                is ChatUiEvent.OnReactionSelected -> Unit
                is ChatUiEvent.OnMessageClicked -> shownTimestamps =
                    if (shownTimestamps.contains(event.messageId)) shownTimestamps - event.messageId
                    else shownTimestamps + event.messageId
                is ChatUiEvent.ShowAttachmentsOverlay -> attachmentsOptionsOpen = true
                is ChatUiEvent.CloseAttachmentsOverlay -> attachmentsOptionsOpen = false
            }
        }
    }

    private fun sendMessage(
        messageText: String,
        messageType: MessageType,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            when (messageType) {
                MessageType.TEXT -> chatRepository.sendTextMessage(messageText)
                    .onSuccess { onSuccess() }
                    .onFailure { _sideEffects.emitSuspended(ChatUiSideEffect.ShowError("The message was not delivered. Please try again.")) }
                MessageType.IMAGE -> Unit
                MessageType.VIDEO -> Unit
                MessageType.VOICE -> Unit
            }
        }
    }
}