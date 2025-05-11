package com.koren.chat.di

import com.koren.chat.util.DefaultThumbnailGenerator
import com.koren.chat.util.ThumbnailGenerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonModule {

    @Singleton
    @Binds
    abstract fun bindThumbnailGenerator(
        thumbnailGenerator: DefaultThumbnailGenerator
    ): ThumbnailGenerator
}