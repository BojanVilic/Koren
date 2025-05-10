package com.koren.chat.ui.chat.messages_window

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.koren.chat.ui.chat.ChatUiSideEffect
import com.koren.common.models.chat.ChatItem
import com.koren.common.services.UserSession
import com.koren.data.repository.ChatRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesWindowPresenter @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userSession: UserSession,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase,
    private val scope: CoroutineScope
) {
    
    @Composable
    fun present(
        sideEffects: MutableSharedFlow<ChatUiSideEffect>,
        listState: LazyListState
    ): MessagesWindowUiState {
        val currentUserId by userSession.currentUser.map { it.id }.collectAsState(initial = "")
        val familyMembers by getAllFamilyMembersUseCase.invoke().collectAsState(initial = emptyList())

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

        if (currentUserId.isBlank()) return MessagesWindowUiState.Loading

        return MessagesWindowUiState.Shown(
            currentUserId = currentUserId,
            listState = listState,
            chatItems = chatItems,
            showReactionPopup = showReactionPopup,
            targetMessageIdForReaction = targetMessageIdForReaction,
            shownTimestamps = shownTimestamps,
            profilePicsMap = profilePicsMap,
            fetchingMore = fetchingMore,
            canFetchMore = canFetchMore
        ) { event ->
            when (event) {
                is MessagesWindowUiEvent.DismissReactionPopup -> {
                    showReactionPopup = false
                    targetMessageIdForReaction = null
                }
                is MessagesWindowUiEvent.OpenMessageReactions -> {
                    showReactionPopup = true
                    targetMessageIdForReaction = event.messageId
                }
                is MessagesWindowUiEvent.OnReactionSelected -> addReactionToMessage(
                    messageId = event.messageId,
                    reaction = event.reaction,
                    onSuccess = { showReactionPopup = false },
                    onFailure = { errorMessage ->
                        scope.launch { sideEffects.emit(ChatUiSideEffect.ShowError(errorMessage)) }
                    }
                )
                is MessagesWindowUiEvent.OnMessageClicked -> shownTimestamps =
                    if (shownTimestamps.contains(event.messageId)) shownTimestamps - event.messageId
                    else shownTimestamps + event.messageId
                is MessagesWindowUiEvent.FetchMoreMessages -> {
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
                is MessagesWindowUiEvent.OpenImageAttachment -> {
                    val mediaUrls = event.message.mediaUrls
                    if (mediaUrls.isNullOrEmpty()) return@Shown
                    if (mediaUrls.size > 1) scope.launch { sideEffects.emit(ChatUiSideEffect.NavigateToImageAttachment(event.message.id)) }
                    else scope.launch { sideEffects.emit(ChatUiSideEffect.NavigateToFullScreenImage(mediaUrls.first())) }
                }
            }
        }
    }

    private fun addReactionToMessage(
        messageId: String,
        reaction: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        scope.launch(Dispatchers.Default) {
            chatRepository.addReactionToMessage(messageId, reaction)
                .onSuccess { onSuccess() }
                .onFailure { onFailure("The reaction was not added. Please try again.") }
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
            .launchIn(scope)
    }
}