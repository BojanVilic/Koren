package com.koren.chat.ui.chat

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.koren.common.models.chat.ChatItem
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ChatRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userSession: UserSession,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase,
    private val messageInputPresent: MessageInputPresent
): MoleculeViewModel<ChatUiEvent, ChatUiState, ChatUiSideEffect>() {

    override fun setInitialState(): ChatUiState = ChatUiState.Loading

    private val events = MutableSharedFlow<ChatUiEvent>()

    @Composable
    override fun produceState(): ChatUiState {
        val currentUserId by userSession.currentUser.map { it.id }.collectAsState(initial = "")
        val familyMembers by getAllFamilyMembersUseCase.invoke().collectAsState(initial = emptyList())
        val listState = rememberLazyListState()

        var chatItems by remember { mutableStateOf<List<ChatItem>>(emptyList()) }
        var showReactionPopup by remember { mutableStateOf(false) }
        var targetMessageIdForReaction by remember { mutableStateOf<String?>(null) }
        var shownTimestamps by remember { mutableStateOf(emptySet<String>()) }
        val profilePicsMap by remember {
            derivedStateOf {
                familyMembers.associate { member ->
                    member.id to member.profilePictureUrl
                }
            }
        }
        var canFetchMore by remember { mutableStateOf(true) }
        var fetchingMore by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            chatRepository.getChatMessages().collect { firstPage ->
                chatItems = firstPage
                fetchingMore = false
            }
        }

        return ChatUiState.Shown(
            currentUserId = currentUserId,
            listState = listState,
            chatItems = chatItems,
            messageInputUiState = messageInputPresent.present(events, _sideEffects, listState),
            showReactionPopup = showReactionPopup,
            targetMessageIdForReaction = targetMessageIdForReaction,
            shownTimestamps = shownTimestamps,
            profilePicsMap = profilePicsMap,
            fetchingMore = fetchingMore,
            canFetchMore = canFetchMore
        ) { event ->
            viewModelScope.launch { events.emit(event) }
            when (event) {
                is ChatUiEvent.DismissReactionPopup -> {
                    showReactionPopup = false
                    targetMessageIdForReaction = null
                }
                is ChatUiEvent.OpenMessageReactions -> {
                    showReactionPopup = true
                    targetMessageIdForReaction = event.messageId
                }
                is ChatUiEvent.OnReactionSelected -> addReactionToMessage(
                    messageId = event.messageId,
                    reaction = event.reaction,
                    onSuccess = { showReactionPopup = false }
                )
                is ChatUiEvent.OnMessageClicked -> shownTimestamps =
                    if (shownTimestamps.contains(event.messageId)) shownTimestamps - event.messageId
                    else shownTimestamps + event.messageId
                is ChatUiEvent.FetchMoreMessages -> {
                    if (canFetchMore) {
                        fetchingMore = true
                        (chatItems.filterIsInstance<ChatItem.MessageItem>()
                            .last() as? ChatItem.MessageItem)
                            ?.let {
                                fetchMoreMessages(
                                    lastTimestamp = -it.message.timestamp,
                                    onResult = { newItems, hasMore ->
                                        chatItems = chatItems + newItems
                                        canFetchMore = hasMore
                                        fetchingMore = false
                                    }
                                )
                            }
                            ?: run { fetchingMore = false }
                    }
                }
                is ChatUiEvent.OpenImageAttachment -> {
                    val mediaUrls = event.message.mediaUrls
                    if (mediaUrls.isNullOrEmpty()) return@Shown
                    if (mediaUrls.size > 1) _sideEffects.emitSuspended(ChatUiSideEffect.NavigateToImageAttachment(event.message.id))
                    else _sideEffects.emitSuspended(ChatUiSideEffect.NavigateToFullScreenImage(mediaUrls.first()))
                }
                else -> Unit
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

    private fun fetchMoreMessages(
        lastTimestamp: Long,
        onResult: (List<ChatItem>, Boolean) -> Unit
    ) {
        chatRepository.getOlderMessages(lastTimestamp)
            .onEach { (newItems, hasMore) ->
                onResult(newItems, hasMore)
            }
            .launchIn(viewModelScope)
    }
}