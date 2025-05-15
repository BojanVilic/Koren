package com.koren.chat.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DefaultAudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioRecorder {

    private var mediaRecorder: MediaRecorder? = null
    private var voiceMessageFile: File? = null
    private var recordingStartTime: Long = 0L

    private fun createRecorderInstance(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    override fun startRecording(): Flow<RecordingStatus> = callbackFlow {
        if (mediaRecorder != null) {
            trySend(RecordingStatus.Error("Recording is already in progress."))
            close()
            return@callbackFlow
        }

        val outputFile = File(context.cacheDir, "voice_message_${System.currentTimeMillis()}.mp3")
        val currentRecorder = createRecorderInstance()
        mediaRecorder = currentRecorder

        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(outputFile).use { fos ->
                    currentRecorder.setOutputFile(fos.fd)
                    currentRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                    currentRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    currentRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    currentRecorder.setAudioEncodingBitRate(256000)
                    currentRecorder.setAudioSamplingRate(48000)
                    currentRecorder.prepare()
                    currentRecorder.start()
                }
            }

            voiceMessageFile = outputFile
            recordingStartTime = System.currentTimeMillis()
            trySend(RecordingStatus.Recording(0L))

            val durationEmitterJob = launch {
                while (isActive && mediaRecorder == currentRecorder) {
                    val elapsedMillis = System.currentTimeMillis() - recordingStartTime
                    trySend(RecordingStatus.Recording(elapsedMillis / 1000))
                    delay(1000L)
                }
            }

            awaitClose {
                durationEmitterJob.cancel()
                if (mediaRecorder == currentRecorder) {
                    try {
                        mediaRecorder?.stop()
                        mediaRecorder?.reset()
                    } catch (e: IllegalStateException) {
                        System.err.println("AudioRecorder: Error stopping/resetting in awaitClose: ${e.message}")
                    } finally {
                        mediaRecorder?.release()
                        mediaRecorder = null
                        if (voiceMessageFile == outputFile) {
                            voiceMessageFile = null
                        }
                    }
                }
            }

        } catch (e: IOException) {
            System.err.println("AudioRecorder: IOException during setup: ${e.message}")
            trySend(RecordingStatus.Error("Setup failed: ${e.message}"))
            withContext(Dispatchers.IO) { // Cleanup might also involve IO
                cleanUpFailedRecording(currentRecorder, outputFile)
            }
            close(e)
        } catch (e: IllegalStateException) {
            System.err.println("AudioRecorder: IllegalStateException during setup/start: ${e.message}")
            trySend(RecordingStatus.Error("Start failed: ${e.message}"))
            withContext(Dispatchers.IO) { // Cleanup might also involve IO
                cleanUpFailedRecording(currentRecorder, outputFile)
            }
            close(e)
        }
    }

    private fun cleanUpFailedRecording(recorder: MediaRecorder?, file: File?) {
        try {
            recorder?.reset()
            recorder?.release()
        } catch (e: Exception) {
            System.err.println("AudioRecorder: Error during cleanup of failed recording: ${e.message}")
        }
        if (this.mediaRecorder == recorder) {
            this.mediaRecorder = null
        }
        file?.delete()
        if (this.voiceMessageFile == file) {
            this.voiceMessageFile = null
        }
    }

    override fun stopRecording(): File? {
        val currentRecorder = mediaRecorder
        val currentFile = voiceMessageFile

        if (currentRecorder == null) return null

        try {
            currentRecorder.stop()
            currentRecorder.reset()
        } catch (e: IllegalStateException) {
            System.err.println("AudioRecorder: Error stopping recorder: ${e.message}")
        } finally {
            currentRecorder.release()
            mediaRecorder = null
            voiceMessageFile = null
            recordingStartTime = 0L
        }
        return currentFile
    }
}