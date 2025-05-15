package com.koren.chat.util

import kotlinx.coroutines.flow.Flow
import java.io.File

internal typealias Seconds = Long

sealed interface RecordingStatus {
    data object Idle : RecordingStatus
    data class Recording(val durationSeconds: Seconds) : RecordingStatus
    data class Error(val message: String) : RecordingStatus
}

interface AudioRecorder {
    fun startRecording(): Flow<RecordingStatus>
    fun stopRecording(): File?
}