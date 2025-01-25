package com.koren.data.repository

import android.location.Location
import com.koren.common.models.activity.LocationActivity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    suspend fun insertNewActivity(location: Location)
    fun getLocationActivities(): Flow<List<LocationActivity>>
}