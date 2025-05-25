package com.koren.chat.util

import android.content.Context
import com.koren.common.services.AudioFileManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class DefaultAudioFileManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) : AudioFileManager {

    override suspend fun getCacheFile(url: String): File {
        val cacheDir = File(context.cacheDir, "voice_messages").apply {
            if (!exists()) mkdirs()
        }
        return File(cacheDir, "voice_${url.hashCode()}.mp3")
    }

    override suspend fun createTempFile(): File {
        val cacheDir = File(context.cacheDir, "voice_messages").apply {
            if (!exists()) mkdirs()
        }
        return File(cacheDir, "voice_${UUID.randomUUID()}.mp3")
    }
}