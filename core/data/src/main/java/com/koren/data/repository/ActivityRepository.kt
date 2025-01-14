package com.koren.data.repository

import com.koren.common.models.activity.LocationActivity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    suspend fun insertNewActivity(activity: LocationActivity)
    fun getActivities(): Flow<List<LocationActivity>>
}