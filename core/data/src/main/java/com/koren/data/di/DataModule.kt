package com.koren.data.di

import com.koren.data.repository.ActivityRepository
import com.koren.data.repository.CalendarRepository
import com.koren.data.repository.DefaultActivityRepository
import com.koren.data.repository.DefaultCalendarRepository
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

    @Binds
    internal abstract fun bindsActivityRepository(
        activityRepository: DefaultActivityRepository
    ): ActivityRepository

    @Binds
    internal abstract fun bindsCalendarRepository(
        calendarRepository: DefaultCalendarRepository
    ): CalendarRepository
}