package com.koren.map.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.services.LocationService
import com.koren.common.services.ResourceProvider
import com.koren.common.services.UserSession
import com.koren.data.repository.ActivityRepository
import com.koren.map.R
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
        placesClient: PlacesClient,
        firebaseDatabase: FirebaseDatabase
    ): LocationService {
        return DefaultLocationService(
            context,
            activityRepository,
            userSession,
            placesClient,
            firebaseDatabase
        )
    }

    @Singleton
    @Provides
    fun providePlacesClient(
        @ApplicationContext context: Context,
        resourceProvider: ResourceProvider
    ): PlacesClient {
        Places.initialize(context, resourceProvider[R.string.google_maps_key])
        return Places.createClient(context)
    }
}