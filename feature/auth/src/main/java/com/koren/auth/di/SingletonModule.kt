package com.koren.auth.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.koren.auth.service.DefaultAuthService
import com.koren.auth.service.EmailAuthService
import com.koren.auth.service.GoogleAuthService
import com.koren.auth.service.UserSessionImpl
import com.koren.common.services.ResourceProvider
import com.koren.common.services.UserSession
import com.koren.data.services.AuthService
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
        credentialManager: CredentialManager,
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth
    ): GoogleAuthService {
        return GoogleAuthService(
            resourceProvider,
            credentialManager,
            context,
            firebaseAuth
        )
    }

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    @Singleton
    fun provideUserSession(): UserSession {
        return UserSessionImpl()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        credentialManager: CredentialManager,
        auth: FirebaseAuth,
        googleAuthService: GoogleAuthService,
        emailAuthService: EmailAuthService,
        userSession: UserSession
    ): AuthService {
        return DefaultAuthService(
            credentialManager,
            auth,
            googleAuthService,
            emailAuthService
        )
    }

    @Provides
    @Singleton
    fun provideEmailAuthService(
        auth: FirebaseAuth
    ): EmailAuthService {
        return EmailAuthService(
            auth
        )
    }
}