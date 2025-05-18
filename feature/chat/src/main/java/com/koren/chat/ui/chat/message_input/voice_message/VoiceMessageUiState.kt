package com.koren.chat.ui.chat.message_input.voice_message

import com.koren.chat.util.RecordingStatus
import com.koren.common.util.EventHandler
import com.koren.common.util.UiEvent
import com.koren.common.util.UiState
import java.io.File

data class VoiceMessageUiState(
    val voiceMessageFile: File? = null,
    val voiceMessageMode: Boolean = false,
    val voiceMessageRecording: Boolean = false,
    val audioRecordingStatus: RecordingStatus = RecordingStatus.Idle,
    val voiceMessageDuration: Long = 0L,
    val playbackState: PlaybackState = PlaybackState.STOPPED,
    override val eventSink: (VoiceMessageUiEvent) -> Unit = {}
): UiState, EventHandler<VoiceMessageUiEvent>

sealed interface VoiceMessageUiEvent : UiEvent {
    data object StartPlayback : VoiceMessageUiEvent
    data object PausePlayback : VoiceMessageUiEvent
    data object ResumePlayback : VoiceMessageUiEvent
    data object StopPlayback : VoiceMessageUiEvent
    data object StartRecording : VoiceMessageUiEvent
    data object StopRecording : VoiceMessageUiEvent
    data object AttachVoiceMessage : VoiceMessageUiEvent
    data object RemoveVoiceMessage : VoiceMessageUiEvent
    data object RestartRecording : VoiceMessageUiEvent
    data object ToggleVoiceRecorder : VoiceMessageUiEvent
}

enum class PlaybackState {
    PLAYING,
    PAUSED,
    STOPPED
}