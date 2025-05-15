package com.koren.chat.util

import java.io.File

interface AudioPlayer {
    fun playFile(file: File, onCompletion: () -> Unit)
    fun pause()
    fun resume()
    fun stop()
}