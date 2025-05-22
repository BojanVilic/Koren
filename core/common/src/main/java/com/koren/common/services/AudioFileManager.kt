package com.koren.common.services

import java.io.File

interface AudioFileManager {
    suspend fun getCacheFile(url: String): File
    suspend fun createTempFile(): File
}