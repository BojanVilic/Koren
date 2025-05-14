package com.koren.chat.util

import kotlinx.coroutines.flow.Flow
import java.io.File

internal typealias Seconds = Long

interface AudioRecorder {
    fun startRecording(): Flow<Seconds>
    fun stopRecording(): File?
}