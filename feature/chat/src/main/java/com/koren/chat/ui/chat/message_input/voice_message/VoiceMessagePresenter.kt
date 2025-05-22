package com.koren.chat.ui.chat.message_input.voice_message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
        var playbackPosition by remember { mutableFloatStateOf(0f) }
        var duration by remember { mutableIntStateOf(0) }
        var playbackState by remember { mutableStateOf(PlaybackState.STOPPED) }
        var attached by remember { mutableStateOf(false) }
        var pendingSeekPosition by remember { mutableStateOf<Float?>(null) }

        return VoiceMessageUiState(
            voiceMessageFile = voiceMessageFile,
            voiceMessageMode = voiceMessageMode,
            voiceMessageRecording = voiceMessageRecording,
            audioRecordingStatus = audioRecordingStatus,
            playbackPosition = playbackPosition,
            duration = duration,
            playbackState = playbackState,
            attached = attached
        ) { event ->
            when (event) {
                is VoiceMessageUiEvent.StartPlayback -> {
                    voiceMessageFile?.let { file ->
                        scope.launch {
                            audioPlayer.playFile(
                                file = file,
                                onCompletion = {
                                    playbackState = PlaybackState.STOPPED
                                    playbackPosition = 0f
                                },
                                startPosition = pendingSeekPosition?.let { (it * duration * 1000).toInt() }
                            ).collect { progress ->
                                playbackPosition = progress
                            }
                        }
                        playbackState = PlaybackState.PLAYING
                        pendingSeekPosition = null
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
                    playbackPosition = 0f
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
                                duration = it.durationSeconds
                                audioRecordingStatus = it
                            }
                    }
                }
                is VoiceMessageUiEvent.StopRecording -> voiceMessageFile = audioRecorder.stopRecording()
                is VoiceMessageUiEvent.AttachVoiceMessage -> {
                    voiceMessageRecording = false
                    attached = true
                    voiceMessageMode = false
                }
                is VoiceMessageUiEvent.RemoveVoiceMessage -> {
                    voiceMessageFile = null
                    duration = 0
                    voiceMessageRecording = false
                }
                is VoiceMessageUiEvent.RestartRecording -> {
                    voiceMessageFile = null
                    duration = 0
                    voiceMessageRecording = false
                    attached = false
                }
                is VoiceMessageUiEvent.SeekTo -> {
                    val seekPosition = event.progress
                    if (playbackState == PlaybackState.PLAYING || playbackState == PlaybackState.PAUSED) {
                        audioPlayer.seekTo((seekPosition * duration * 1000).toInt())
                    } else {
                        pendingSeekPosition = seekPosition
                    }
                    playbackPosition = seekPosition
                }
                is VoiceMessageUiEvent.Reset -> {
                    voiceMessageFile = null
                    duration = 0
                    playbackPosition = 0f
                    playbackState = PlaybackState.STOPPED
                    audioPlayer.stop()
                    audioRecorder.stopRecording()
                    voiceMessageRecording = false
                    voiceMessageMode = false
                    attached = false
                }
            }
        }
    }
}