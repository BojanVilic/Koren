package com.koren.chat.ui.chat.message_input.voice_message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.koren.chat.util.AudioPlayer
import com.koren.chat.util.AudioRecorder
import com.koren.chat.util.RecordingStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class VoiceMessagePresenter @Inject constructor(
    private val audioPlayer: AudioPlayer,
    private val audioRecorder: AudioRecorder,
    private val scope: CoroutineScope
) {

    @Composable
    fun present(): VoiceMessageUiState {
        var voiceMessageMode by remember { mutableStateOf(false) }
        var voiceMessageRecording by remember { mutableStateOf(false) }
        var audioRecordingStatus by remember { mutableStateOf<RecordingStatus>(RecordingStatus.Idle) }
        var voiceMessageFile by remember { mutableStateOf<File?>(null) }
        var voiceMessageDuration by remember { mutableLongStateOf(0L) }
        var playbackState by remember { mutableStateOf(PlaybackState.STOPPED) }

        return VoiceMessageUiState(
            voiceMessageFile = voiceMessageFile,
            voiceMessageMode = voiceMessageMode,
            voiceMessageRecording = voiceMessageRecording,
            audioRecordingStatus = audioRecordingStatus,
            voiceMessageDuration = voiceMessageDuration,
            playbackState = playbackState
        ) { event ->
            when (event) {
                is VoiceMessageUiEvent.StartPlayback -> {
                    voiceMessageFile?.let {
                        audioPlayer.playFile(it) {
                            playbackState = PlaybackState.STOPPED
                        }
                        playbackState = PlaybackState.PLAYING
                    }
                }
                is VoiceMessageUiEvent.PausePlayback -> {
                    audioPlayer.pause()
                    playbackState = PlaybackState.PAUSED
                }
                is VoiceMessageUiEvent.ResumePlayback -> {
                    audioPlayer.resume()
                    playbackState = PlaybackState.PLAYING
                }
                is VoiceMessageUiEvent.StopPlayback -> {
                    audioPlayer.stop()
                    playbackState = PlaybackState.STOPPED
                }
                is VoiceMessageUiEvent.ToggleVoiceRecorder -> {
                    if (voiceMessageRecording) {
                        voiceMessageRecording = false
                        scope.launch(Dispatchers.IO) {
                            voiceMessageFile = audioRecorder.stopRecording()
                        }
                    }
                    voiceMessageMode = !voiceMessageMode
                }
                is VoiceMessageUiEvent.StartRecording -> {
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
                is VoiceMessageUiEvent.StopRecording -> voiceMessageFile = audioRecorder.stopRecording()
                is VoiceMessageUiEvent.AttachVoiceMessage -> voiceMessageRecording = false
                is VoiceMessageUiEvent.RemoveVoiceMessage -> {
                    voiceMessageFile = null
                    voiceMessageDuration = 0L
                }
                is VoiceMessageUiEvent.RestartRecording -> {
                    voiceMessageFile = null
                    voiceMessageDuration = 0L
                    voiceMessageRecording = false
                }
            }
        }
    }
}