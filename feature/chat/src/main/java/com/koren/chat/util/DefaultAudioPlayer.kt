package com.koren.chat.util

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class DefaultAudioPlayer @Inject constructor(
    @ApplicationContext
    private val context: Context
): AudioPlayer {

    private var player: MediaPlayer? = null

    override fun playFile(file: File, onCompletion: () -> Unit) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            setOnCompletionListener {
                release()
                onCompletion()
                player = null
            }
            start()
        }
    }

    override fun pause() {
        player?.pause()
    }

    override fun resume() {
        player?.start()
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}