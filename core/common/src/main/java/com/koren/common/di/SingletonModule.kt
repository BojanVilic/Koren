package com.koren.common.di

import android.content.Context
import com.koren.common.services.ResourceProvider
import com.koren.common.services.app_info.AppInfoProvider
import com.koren.common.services.app_info.DefaultAppInfoProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider = ResourceProvider(context)

    @Provides
    @Singleton
    fun provideAppInfoProvider(@ApplicationContext context: Context): AppInfoProvider = DefaultAppInfoProvider(context)
}