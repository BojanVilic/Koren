package com.koren.chat.di

import com.koren.common.services.AudioFileManager
import com.koren.chat.util.AudioPlayer
import com.koren.chat.util.AudioRecorder
import com.koren.chat.util.DefaultAudioFileManager
import com.koren.chat.util.DefaultAudioPlayer
import com.koren.chat.util.DefaultAudioRecorder
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

    @Singleton
    @Binds
    abstract fun bindAudioRecorder(
        audioRecorder: DefaultAudioRecorder
    ): AudioRecorder

    @Singleton
    @Binds
    abstract fun bindAudioPlayer(
        audioPlayer: DefaultAudioPlayer
    ): AudioPlayer

    @Singleton
    @Binds
    abstract fun bindAudioFileManager(
        audioFileManager: DefaultAudioFileManager
    ): AudioFileManager
}