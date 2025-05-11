package com.koren.chat.util

import android.graphics.Bitmap

interface ThumbnailGenerator {
    suspend fun generateThumbnail(videoUri: String): Result<Bitmap>
}