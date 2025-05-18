@file:OptIn(ExperimentalPermissionsApi::class)

package com.koren.chat.ui.chat.message_input.voice_message

import android.Manifest.permission.RECORD_AUDIO
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.koren.chat.R
import com.koren.chat.util.RecordingStatus
import com.koren.common.util.DateUtils
import com.koren.designsystem.icon.Attach
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Mic
import com.koren.designsystem.icon.Pause
import com.koren.designsystem.icon.Play
import com.koren.designsystem.icon.Restart
import com.koren.designsystem.icon.Stop
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import java.io.File

@Composable
internal fun VoiceRecorderAre(
    uiState: VoiceMessageUiState
) {
    when {
        uiState.voiceMessageFile != null -> VoiceMessagePlayback(uiState)
        uiState.voiceMessageRecording -> VoiceRecorder(uiState)
        else -> StartRecordingPrompt(uiState)
    }
}

@Composable
internal fun StartRecordingPrompt(
    uiState: VoiceMessageUiState
) {
    val permissionState = rememberPermissionState(RECORD_AUDIO)

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            onClick = {
                if (permissionState.status.isGranted) uiState.eventSink(VoiceMessageUiEvent.StartRecording)
                else permissionState.launchPermissionRequest()
            }
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.voice_message_illustration),
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "Tap to record your voice message",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { uiState.eventSink(VoiceMessageUiEvent.ToggleVoiceRecorder) }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        if (permissionState.status.isGranted) uiState.eventSink(VoiceMessageUiEvent.StartRecording)
                        else permissionState.launchPermissionRequest()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = KorenIcons.Mic,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Record",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
internal fun VoiceRecorder(
    uiState: VoiceMessageUiState
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (uiState.audioRecordingStatus) {
                    is RecordingStatus.Error -> {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "Error recording audio.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    is RecordingStatus.Idle -> Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Starting recording...",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    is RecordingStatus.Recording -> {
                        RippleDot()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = DateUtils.formatDuration(uiState.audioRecordingStatus.durationSeconds),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { uiState.eventSink(VoiceMessageUiEvent.ToggleVoiceRecorder) }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { uiState.eventSink(VoiceMessageUiEvent.StopRecording) }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = KorenIcons.Stop,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stop",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun RippleDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val ripple1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 3.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1Scale"
    )

    val ripple1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1Alpha"
    )

    val dotScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    val color = MaterialTheme.colorScheme.primary
    val baseDotSize = 16.dp

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(baseDotSize)
                .scale(ripple1Scale)
                .alpha(ripple1Alpha)
                .background(color, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(baseDotSize)
                .scale(dotScale)
                .background(color, CircleShape)
        )
    }
}

@Composable
fun VoiceMessagePlayback(uiState: VoiceMessageUiState) {
    val voiceMessageFile = uiState.voiceMessageFile
    val progress = 0.5f

    if (voiceMessageFile != null) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            when (uiState.playbackState) {
                                PlaybackState.PLAYING -> uiState.eventSink(VoiceMessageUiEvent.PausePlayback)
                                PlaybackState.PAUSED -> uiState.eventSink(VoiceMessageUiEvent.ResumePlayback)
                                PlaybackState.STOPPED -> uiState.eventSink(VoiceMessageUiEvent.StartPlayback)
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = if (uiState.playbackState == PlaybackState.PLAYING) KorenIcons.Pause else KorenIcons.Play,
                            contentDescription = "Play"
                        )
                    }
                    Slider(
                        value = progress,
                        onValueChange = { /* Handle seek logic */ },
                        modifier = Modifier.weight(1f),
                        valueRange = 0f..1f
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DateUtils.formatDuration(uiState.voiceMessageDuration),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { uiState.eventSink(VoiceMessageUiEvent.AttachVoiceMessage) }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = KorenIcons.Attach,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Attach",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { uiState.eventSink(VoiceMessageUiEvent.RestartRecording) }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = KorenIcons.Restart,
                            contentDescription = "Restart"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Restart")
                    }
                }
            }
        }
    }
}

@ThemePreview
@Composable
private fun VoiceMessagePlaybackPreview() {
    KorenTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            VoiceMessagePlayback(
                uiState = VoiceMessageUiState(
                    voiceMessageFile = File.createTempFile("test", "test"),
                    playbackState = PlaybackState.PLAYING,
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
private fun StartRecordingPromptPreview() {
    KorenTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            StartRecordingPrompt(
                uiState = VoiceMessageUiState(
                    voiceMessageMode = true,
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
private fun VoiceRecorderPreview() {
    KorenTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            VoiceRecorder(
                uiState = VoiceMessageUiState(
                    voiceMessageMode = true,
                    eventSink = {}
                )
            )
        }
    }
}