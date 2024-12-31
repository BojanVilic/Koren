package com.koren.data.di

import com.koren.data.repository.DefaultInvitationRepository
import com.koren.data.repository.InvitationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsInvitationRepository(
        invitationRepository: DefaultInvitationRepository
    ): InvitationRepository
}