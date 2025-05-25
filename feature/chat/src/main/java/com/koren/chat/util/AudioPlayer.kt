package com.koren.chat.util

import kotlinx.coroutines.flow.Flow
import java.io.File

interface AudioPlayer {
    fun playFile(file: File, onCompletion: () -> Unit, startPosition: Int? = null): Flow<Float>
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(position: Int)
    fun getDuration(): Int
}