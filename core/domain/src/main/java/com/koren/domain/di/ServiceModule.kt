package com.koren.domain.di

import com.koren.common.services.BatteryService
import com.koren.domain.services.DefaultBatteryService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Singleton
    @Binds
    abstract fun bindBatteryService(
        defaultBatteryService: DefaultBatteryService
    ): BatteryService
}