package com.koren.chat.ui.chat.messages_window

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.koren.chat.ui.chat.ChatUiSideEffect
import com.koren.chat.ui.chat.message_input.voice_message.PlaybackState
import com.koren.chat.util.AudioPlayer
import com.koren.common.models.chat.ChatItem
import com.koren.common.models.chat.ChatMessage
import com.koren.common.services.UserSession
import com.koren.data.repository.ChatRepository
import com.koren.domain.GetAllFamilyMembersUseCase
import kotlinx.coroutines.CoroutineScope
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
    private val scope: CoroutineScope,
    private val audioPlayer: AudioPlayer
) {
    
    @Composable
    fun present(
        sideEffects: MutableSharedFlow<ChatUiSideEffect>,
        listState: LazyListState
    ): MessagesWindowUiState {
        val currentUserId by userSession.currentUser.map { it.id }.collectAsState(initial = "")
        val familyMembers by getAllFamilyMembersUseCase.invoke().collectAsState(initial = emptyList())

        var chatItems by remember { mutableStateOf<List<ChatItem>>(emptyList()) }
        var shownTimestamps by remember { mutableStateOf(emptySet<String>()) }
        val profilePicsMap by remember {
            derivedStateOf {
                familyMembers
                    .filter { it.id.isNotBlank() && it.displayName.isNotBlank() }
                    .map { member ->
                        MessageSenderInfo(
                            id = member.id,
                            name = member.displayName,
                            profilePictureUrl = member.profilePictureUrl
                        )
                    }
                    .toSet()
            }
        }
        var canFetchMore by remember { mutableStateOf(true) }
        var fetchingMore by remember { mutableStateOf(false) }
        var playbackPosition by remember { mutableFloatStateOf(0f) }
        var pendingSeekPosition by remember { mutableStateOf<Float?>(null) }
        var currentVoiceMessage by remember { mutableStateOf<ChatMessage?>(null) }
        var playbackState by remember { mutableStateOf(PlaybackState.STOPPED) }

        LaunchedEffect(currentVoiceMessage?.id) {
            if (currentVoiceMessage != null) {
                chatRepository.downloadAudioMessage(currentVoiceMessage?.mediaUrls?.first()?: "").getOrNull()?.let { file ->
                    scope.launch {
                        audioPlayer.playFile(
                            file = file,
                            onCompletion = {
                                playbackState = PlaybackState.STOPPED
                                playbackPosition = 0f
                                currentVoiceMessage = null
                            },
                            startPosition = pendingSeekPosition?.let { (it * (currentVoiceMessage?.mediaDuration?: 0L) * 1000).toInt() }
                        ).collect { progress ->
                            playbackPosition = progress
                        }
                    }
                    playbackState = PlaybackState.PLAYING
                    pendingSeekPosition = null
                }
            }
        }

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
            shownTimestamps = shownTimestamps,
            messageSenderInfo = profilePicsMap,
            fetchingMore = fetchingMore,
            canFetchMore = canFetchMore,
            playbackPosition = playbackPosition,
            currentlyPlayingMessageId = currentVoiceMessage?.id,
            playbackState = playbackState
        ) { event ->
            when (event) {
                is MessagesWindowUiEvent.OpenMoreOptions -> {
                    scope.launch { sideEffects.emit(ChatUiSideEffect.NavigateToMoreOptions(event.messageId)) }
                }
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
                is MessagesWindowUiEvent.OpenVideoAttachment -> {
                    val mediaUrls = event.message.mediaUrls
                    if (mediaUrls.isNullOrEmpty()) return@Shown
                    else scope.launch { sideEffects.emit(ChatUiSideEffect.NavigateToFullScreenVideo(mediaUrls.first())) }
                }
                is MessagesWindowUiEvent.StartPlayback -> currentVoiceMessage = event.voiceMessage
                is MessagesWindowUiEvent.SeekVoiceMessageTo -> {
                    val seekPosition = event.progress
                    if (playbackState == PlaybackState.PLAYING || playbackState == PlaybackState.PAUSED) {
                        audioPlayer.seekTo((seekPosition * (currentVoiceMessage?.mediaDuration?: 0L) * 1000).toInt())
                    } else {
                        pendingSeekPosition = seekPosition
                    }
                    playbackPosition = seekPosition
                }
                is MessagesWindowUiEvent.PausePlayback -> {
                    audioPlayer.pause()
                    playbackState = PlaybackState.PAUSED
                }
                is MessagesWindowUiEvent.ResumePlayback -> {
                    audioPlayer.resume()
                    playbackState = PlaybackState.PLAYING
                }
            }
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