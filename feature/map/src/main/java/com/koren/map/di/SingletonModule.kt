package com.koren.map.di

import android.content.Context
import com.koren.common.services.LocationService
import com.koren.common.services.ResourceProvider
import com.koren.common.services.UserSession
import com.koren.data.repository.ActivityRepository
import com.koren.map.service.DefaultLocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Singleton
    @Provides
    fun provideLocationService(
        @ApplicationContext context: Context,
        activityRepository: ActivityRepository,
        userSession: UserSession,
        resourceProvider: ResourceProvider
    ): LocationService {
        return DefaultLocationService(
            context,
            activityRepository,
            userSession,
            resourceProvider
        )
    }
}