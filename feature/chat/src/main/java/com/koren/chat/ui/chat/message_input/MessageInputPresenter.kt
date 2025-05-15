package com.koren.chat.ui.chat.message_input

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.koren.chat.ui.chat.ChatUiSideEffect
import com.koren.chat.util.AudioPlayer
import com.koren.chat.util.AudioRecorder
import com.koren.chat.util.RecordingStatus
import com.koren.chat.util.ThumbnailGenerator
import com.koren.common.models.chat.MessageType
import com.koren.data.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MessageInputPresenter @Inject constructor(
    private val chatRepository: ChatRepository,
    private val scope: CoroutineScope,
    private val thumbnailGenerator: ThumbnailGenerator,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer
) {

    @Composable
    fun present(
        sideEffects: MutableSharedFlow<ChatUiSideEffect>,
        listState: LazyListState,
    ): MessageInputUiState {
        val coroutineScope = rememberCoroutineScope()

        var messageText by remember { mutableStateOf(TextFieldValue("")) }
        var sendingMessage by remember { mutableStateOf(false) }
        var imageAttachments by remember { mutableStateOf(emptySet<Uri>()) }
        var attachmentsOptionsOpen by remember { mutableStateOf(false) }
        var videoAttachment by remember { mutableStateOf<Uri?>(null) }
        var videoDuration by remember { mutableLongStateOf(0L) }
        var videoThumbnail by remember { mutableStateOf<Bitmap?>(null) }
        var voiceMessageMode by remember { mutableStateOf(false) }
        var voiceMessageRecording by remember { mutableStateOf(false) }
        var audioRecordingStatus by remember { mutableStateOf<RecordingStatus>(RecordingStatus.Idle) }
        var voiceMessageFile by remember { mutableStateOf<File?>(null) }
        var voiceMessageDuration by remember { mutableLongStateOf(0L) }
        var playbackState by remember { mutableStateOf(PlaybackState.STOPPED) }

        LaunchedEffect(videoAttachment) {
            videoAttachment?.let { uri ->
                thumbnailGenerator.generateThumbnail(uri.toString())
                    .onSuccess { bitmap ->
                        withContext(Dispatchers.Main) {
                            videoThumbnail = bitmap
                        }
                    }
                    .onFailure { sideEffects.emit(ChatUiSideEffect.ShowError("Failed to generate video thumbnail")) }
            }
        }

        return MessageInputUiState(
            messageText = messageText,
            sendingMessage = sendingMessage,
            imageAttachments = imageAttachments,
            attachmentsOverlayShown = attachmentsOptionsOpen,
            videoAttachment = videoAttachment,
            videoDuration = videoDuration,
            videoThumbnail = videoThumbnail,
            voiceMessageMode = voiceMessageMode,
            voiceMessageRecording = voiceMessageRecording,
            audioRecordingStatus = audioRecordingStatus,
            voiceMessageFile = voiceMessageFile,
            voiceMessageDuration = voiceMessageDuration,
            playbackState = playbackState
        ) { event ->
            when (event) {
                is MessageInputUiEvent.OnMessageTextChanged -> messageText = event.text
                is MessageInputUiEvent.SendMessage -> {
                    scope.launch(Dispatchers.Default) {
                        sendingMessage = true
                        sendMessage(
                            messageText = messageText.text,
                            imageAttachments = imageAttachments,
                            onSuccess = {
                                messageText = TextFieldValue("")
                                imageAttachments = emptySet()
                                sendingMessage = false
                                coroutineScope.launch(Dispatchers.IO) {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            onFailure = { errorMessage ->
                                scope.launch {
                                    sideEffects.emit(ChatUiSideEffect.ShowError(errorMessage))
                                }
                                sendingMessage = false
                            },
                            videoUri = videoAttachment,
                            videoDuration = videoDuration,
                            videoThumbnailUri = videoThumbnail
                        )
                    }
                }
                is MessageInputUiEvent.AddImageAttachment -> {
                    imageAttachments = imageAttachments + event.imageUri
                    attachmentsOptionsOpen = false
                }
                is MessageInputUiEvent.RemoveImageAttachment -> imageAttachments = imageAttachments.minus(event.imageUri)
                is MessageInputUiEvent.ShowAttachmentsOverlay -> attachmentsOptionsOpen = true
                is MessageInputUiEvent.CloseAttachmentsOverlay -> attachmentsOptionsOpen = false
                is MessageInputUiEvent.AddVideoAttachment -> {
                    videoAttachment = event.videoUri
                    videoDuration = event.duration
                }
                is MessageInputUiEvent.RemoveVideoAttachment -> videoAttachment = null
                is MessageInputUiEvent.ToggleVoiceRecorder -> {
                    if (voiceMessageRecording) {
                        voiceMessageRecording = false
                        scope.launch(Dispatchers.IO) {
                            voiceMessageFile = audioRecorder.stopRecording()
                        }
                    }
                    voiceMessageMode = !voiceMessageMode
                }
                is MessageInputUiEvent.StartRecording -> {
                    voiceMessageRecording = true
                    scope.launch(Dispatchers.IO) {
                        audioRecorder.startRecording()
                            .filterIsInstance<RecordingStatus.Recording>()
                            .collect {
                                voiceMessageDuration = it.durationSeconds
                                audioRecordingStatus = it
                            }
                    }
                }
                is MessageInputUiEvent.StopRecording -> voiceMessageFile = audioRecorder.stopRecording()
                is MessageInputUiEvent.AttachVoiceMessage -> voiceMessageRecording = false
                is MessageInputUiEvent.RemoveVoiceMessage -> {
                    voiceMessageFile = null
                    voiceMessageDuration = 0L
                }
                is MessageInputUiEvent.RestartRecording -> {
                    voiceMessageFile = null
                    voiceMessageDuration = 0L
                    voiceMessageRecording = false
                }
                is MessageInputUiEvent.StartPlayback -> voiceMessageFile?.let {
                    audioPlayer.playFile(
                        file = it,
                        onCompletion = {
                            playbackState = PlaybackState.STOPPED
                        }
                    )
                    playbackState = PlaybackState.PLAYING
                }
                is MessageInputUiEvent.PausePlayback -> {
                    audioPlayer.pause()
                    playbackState = PlaybackState.PAUSED
                }
                is MessageInputUiEvent.ResumePlayback -> {
                    audioPlayer.resume()
                    playbackState = PlaybackState.PLAYING
                }
            }
        }
    }

    private suspend fun sendMessage(
        messageText: String,
        imageAttachments: Set<Uri> = emptySet(),
        onSuccess: () -> Unit,
        onFailure: (errorMessage: String) -> Unit,
        videoUri: Uri? = null,
        videoDuration: Long = 0L,
        videoThumbnailUri: Bitmap? = null
    ) {
        val messageType = getMessageType(messageText, imageAttachments, videoUri)
        when (messageType) {
            MessageType.TEXT -> chatRepository.sendTextMessage(messageText)
                .onSuccess { onSuccess() }
                .onFailure { onFailure("The message was not delivered. Please try again.") }
            MessageType.IMAGE -> chatRepository.sendImageMessage(imageAttachments, messageText)
                .onSuccess { onSuccess() }
                .onFailure { onFailure("The image was not delivered. Please try again.") }
            MessageType.VIDEO -> chatRepository.sendVideoMessage(videoUri ?: Uri.EMPTY, videoThumbnailUri, videoDuration)
                .onSuccess { onSuccess() }
                .onFailure { onFailure("The video was not delivered. Please try again.") }
            MessageType.VOICE -> Unit
        }
    }

    private fun getMessageType(
        messageText: String,
        imageAttachments: Set<Uri>,
        videoAttachment: Uri? = null
    ): MessageType {
        return when {
            videoAttachment != null -> MessageType.VIDEO
            imageAttachments.isNotEmpty() -> MessageType.IMAGE
            messageText.isNotBlank() -> MessageType.TEXT
            else -> MessageType.TEXT
        }
    }
}