package com.koren.chat.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
class DefaultAudioRecorder @Inject constructor(
    @ApplicationContext
    private val context: Context
) : AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var voiceMessageFile: File? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun startRecording(): Flow<Seconds> {
        createRecorder().apply {
            val outputFile = File(context.cacheDir, "voice_message.mp3")

            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            voiceMessageFile = outputFile
            recorder = this
        }

        return flow {
            var seconds = 0L
            while (true) {
                emit(seconds)
                delay(1000L)
                seconds++
            }
        }
    }

    override fun stopRecording(): File? {
        recorder?.stop()
        recorder?.reset()
        recorder = null

        return voiceMessageFile
    }
}