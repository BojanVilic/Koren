package com.koren.chat.ui.chat.message_input

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.koren.chat.R
import com.koren.common.util.DateUtils
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Mic
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

@Composable
internal fun VoiceRecorderAre(
    uiState: MessageInputUiState
) {
    if (uiState.voiceMessageRecording) VoiceRecorder(uiState)
    else StartRecordingPrompt(uiState)
}

@Composable
internal fun StartRecordingPrompt(
    uiState: MessageInputUiState
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            onClick = {
                uiState.eventSink(MessageInputUiEvent.StartRecording)
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
                    onClick = { uiState.eventSink(MessageInputUiEvent.ToggleVoiceRecorder) }
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
                    onClick = { uiState.eventSink(MessageInputUiEvent.StartRecording) }
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
    uiState: MessageInputUiState
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RippleDot()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = DateUtils.formatDuration(uiState.voiceMessageDuration),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
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
                    onClick = { uiState.eventSink(MessageInputUiEvent.ToggleVoiceRecorder) }
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
                    onClick = { uiState.eventSink(MessageInputUiEvent.StartRecording) }
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

@ThemePreview
@Composable
private fun StartRecordingPromptPreview() {
    KorenTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            StartRecordingPrompt(
                uiState = MessageInputUiState(
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
                uiState = MessageInputUiState(
                    voiceMessageMode = true,
                    eventSink = {}
                )
            )
        }
    }
}