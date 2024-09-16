package com.koren.auth.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.koren.auth.service.GoogleAuthService
import com.koren.auth.service.UserSessionImpl
import com.koren.common.services.ResourceProvider
import com.koren.common.services.UserSession
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
    fun provideFirebaseAuthInstance() = Firebase.auth

    @Provides
    @Singleton
    fun provideGoogleAuthService(
        resourceProvider: ResourceProvider,
        @ApplicationContext context: Context
    ): GoogleAuthService {
        return GoogleAuthService(
            resourceProvider,
            Identity.getSignInClient(context)
        )
    }

    @Provides
    @Singleton
    fun provideUserSession(): UserSession {
        return UserSessionImpl()
    }

}