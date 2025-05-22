package com.koren.chat.util

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class DefaultAudioPlayer @Inject constructor(
    @ApplicationContext
    private val context: Context
): AudioPlayer {

    private var player: MediaPlayer? = null

    override fun playFile(file: File, onCompletion: () -> Unit, startPosition: Int?): Flow<Float> = callbackFlow {
        if (!file.exists() || file.length() == 0L) {
            close(IllegalStateException("Audio file doesn't exist or is empty"))
            return@callbackFlow
        }

        val mediaPlayer = MediaPlayer.create(context, file.toUri()).apply {
            player = this
            setOnCompletionListener {
                trySend(1f)
                release()
                onCompletion()
                player = null
                close()
            }
            startPosition?.let { seekTo(it) }
            start()
        }

        val duration = mediaPlayer.duration
        if (duration <= 0) {
            close(IllegalStateException("Invalid media duration"))
            return@callbackFlow
        }

        val positionUpdateJob = launch {
            while (isActive && mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition
                trySend(currentPosition / duration.toFloat())
                delay(500L)
            }
        }

        awaitClose {
            positionUpdateJob.cancel()
            mediaPlayer.release()
            player = null
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

    override fun seekTo(position: Int) {
        player?.seekTo(position)
    }

    override fun getDuration(): Int {
        return player?.duration ?: 0
    }
}