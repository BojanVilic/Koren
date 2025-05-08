package com.koren.chat.ui

import android.net.Uri
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
        val chatItems by chatRepository.getChatMessages().collectAsState(initial = emptyList())
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
        var imageAttachments by remember { mutableStateOf(emptySet<Uri>()) }
        var sendingMessage by remember { mutableStateOf(false) }

        return ChatUiState.Shown(
            currentUserId = currentUserId,
            chatItems = chatItems,
            messageText = messageText,
            showReactionPopup = showReactionPopup,
            targetMessageIdForReaction = targetMessageIdForReaction,
            shownTimestamps = shownTimestamps,
            attachmentsOverlayShown = attachmentsOptionsOpen,
            profilePicsMap = profilePicsMap,
            imageAttachments = imageAttachments,
            sendingMessage = sendingMessage,
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
                is ChatUiEvent.SendMessage -> {
                    sendingMessage = true
                    sendMessage(
                        messageText = messageText.text,
                        messageType = getMessageType(
                            messageText = messageText.text,
                            imageAttachments = imageAttachments
                        ),
                        imageAttachments = imageAttachments,
                        onSuccess = {
                            messageText = TextFieldValue("")
                            imageAttachments = emptySet()
                            sendingMessage = false
                        }
                    )
                }
                is ChatUiEvent.OnReactionSelected -> addReactionToMessage(
                    messageId = event.messageId,
                    reaction = event.reaction,
                    onSuccess = { showReactionPopup = false }
                )
                is ChatUiEvent.OnMessageClicked -> shownTimestamps =
                    if (shownTimestamps.contains(event.messageId)) shownTimestamps - event.messageId
                    else shownTimestamps + event.messageId
                is ChatUiEvent.ShowAttachmentsOverlay -> attachmentsOptionsOpen = true
                is ChatUiEvent.CloseAttachmentsOverlay -> attachmentsOptionsOpen = false
                is ChatUiEvent.AddImageAttachment -> {
                    imageAttachments = imageAttachments + event.imageUri
                    attachmentsOptionsOpen = false
                }
                is ChatUiEvent.RemoveImageAttachment -> imageAttachments = imageAttachments.minus(event.imageUri)
            }
        }
    }

    private fun sendMessage(
        messageText: String,
        messageType: MessageType,
        imageAttachments: Set<Uri> = emptySet(),
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            when (messageType) {
                MessageType.TEXT -> chatRepository.sendTextMessage(messageText)
                    .onSuccess { onSuccess() }
                    .onFailure { _sideEffects.emitSuspended(ChatUiSideEffect.ShowError("The message was not delivered. Please try again.")) }
                MessageType.IMAGE -> chatRepository.sendImageMessage(imageAttachments, messageText)
                    .onSuccess { onSuccess() }
                    .onFailure { _sideEffects.emitSuspended(ChatUiSideEffect.ShowError("The image was not delivered. Please try again.")) }
                MessageType.VIDEO -> Unit
                MessageType.VOICE -> Unit
            }
        }
    }

    private fun addReactionToMessage(
        messageId: String,
        reaction: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            chatRepository.addReactionToMessage(messageId, reaction)
                .onSuccess { onSuccess() }
                .onFailure { _sideEffects.emitSuspended(ChatUiSideEffect.ShowError("The reaction was not added. Please try again.")) }
        }
    }

    private fun getMessageType(
        messageText: String,
        imageAttachments: Set<Uri>
    ): MessageType {
        return when {
            imageAttachments.isNotEmpty() -> MessageType.IMAGE
            messageText.isNotBlank() -> MessageType.TEXT
            else -> MessageType.TEXT
        }
    }
}