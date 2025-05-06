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
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.models.chat.MessageType
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ChatRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userSession: UserSession,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase
): MoleculeViewModel<ChatUiEvent, ChatUiState, ChatUiSideEffect>() {

    override fun setInitialState(): ChatUiState = ChatUiState.Loading

    /**
     * chatItems$delegate = {ParcelableSnapshotMutableState@38946}  size = 8
     *  0 = {ChatItem$MessageItem@39852} MessageItem(message=ChatMessage(id=9c246e91-d3eb-45c0-be87-bf5abb47276a, senderId=sflWHDcewUhdzaAuFwk511ZyHjW2, timestamp=1746485235081, messageType=TEXT, textContent=Pišev vam svima. Ćao!, mediaUrl=null, mediaDuration=null, reactions=null), id=9c246e91-d3eb-45c0-be87-bf5abb47276a)
     *  1 = {ChatItem$MessageItem@39853} MessageItem(message=ChatMessage(id=0c3d3f36-a82a-4fa1-add6-3e0f8caa2b3f, senderId=sflWHDcewUhdzaAuFwk511ZyHjW2, timestamp=1746450736642, messageType=TEXT, textContent=Nemojte tako., mediaUrl=null, mediaDuration=null, reactions=null), id=0c3d3f36-a82a-4fa1-add6-3e0f8caa2b3f)
     *  2 = {ChatItem$MessageItem@39854} MessageItem(message=ChatMessage(id=7510904a-7599-4b29-ac12-19321fcb3904, senderId=vJpINtZcoaPoK1cKC1UWHdW1oFM2, timestamp=1746450472439, messageType=TEXT, textContent=Majstore, je l' gotovo ono moje?, mediaUrl=null, mediaDuration=null, reactions=null), id=7510904a-7599-4b29-ac12-19321fcb3904)
     *  3 = {ChatItem$MessageItem@39855} MessageItem(message=ChatMessage(id=5dea5326-0b3f-4a4a-be90-7e5e2f0181e0, senderId=fFkinGTCtsg40PsDPCEeQFYys1h2, timestamp=1746450242839, messageType=TEXT, textContent=Dodji sutra Bane., mediaUrl=null, mediaDuration=null, reactions=null), id=5dea5326-0b3f-4a4a-be90-7e5e2f0181e0)
     *  4 = {ChatItem$MessageItem@39856} MessageItem(message=ChatMessage(id=9c29b6a3-ab9b-4b30-a9da-415537132a52, senderId=fFkinGTCtsg40PsDPCEeQFYys1h2, timestamp=1746450238122, messageType=TEXT, textContent=Doprinosim!, mediaUrl=null, mediaDuration=null, reactions=null), id=9c29b6a3-ab9b-4b30-a9da-415537132a52)
     *  5 = {ChatItem$DateSeparator@39851} DateSeparator(timestamp=1746485235081, id=date_1746485235081)
     *  6 = {ChatItem$MessageItem@39858} MessageItem(message=ChatMessage(id=286639ed-aea5-486e-a1d6-8cd3148fd8e5, senderId=966Q7GMkIJgl4C8uqvenIdZFN8F3, timestamp=1746416808186, messageType=TEXT, textContent=asd, mediaUrl=null, mediaDuration=null, reactions=null), id=286639ed-aea5-486e-a1d6-8cd3148fd8e5)
     *  7 = {ChatItem$DateSeparator@39857} DateSeparator(timestamp=1746416808186, id=date_1746416808186)
     */

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

        return ChatUiState.Shown(
            currentUserId = currentUserId,
            chatItems = chatItems,
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