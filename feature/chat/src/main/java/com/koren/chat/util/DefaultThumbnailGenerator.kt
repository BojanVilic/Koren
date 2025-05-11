package com.koren.chat.util

import android.content.Context
import android.graphics.Bitmap
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import coil3.video.VideoFrameDecoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultThumbnailGenerator @Inject constructor(
    @ApplicationContext
    private val context: Context
): ThumbnailGenerator {

    override suspend fun generateThumbnail(videoUri: String): Result<Bitmap> {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()

        val request = ImageRequest.Builder(context)
            .data(videoUri)
            .size(256, 256)
            .build()

        return withContext(Dispatchers.IO) {
            imageLoader.execute(request).image?.toBitmap()
                ?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("Failed to generate thumbnail"))
        }
    }
}