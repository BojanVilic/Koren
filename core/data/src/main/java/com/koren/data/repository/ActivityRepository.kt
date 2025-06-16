package com.koren.data.repository

import android.location.Location
import com.koren.common.models.activity.LocationActivity
import com.koren.common.models.activity.UserLocationActivity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    suspend fun insertNewActivity(location: Location)
    fun getLocationActivities(): Flow<List<UserLocationActivity>>
    fun getMoreLocationActivities(lastCreatedAt: Long): Flow<Pair<List<UserLocationActivity>, Boolean>>
    suspend fun getLocationActivityById(activityId: String): LocationActivity
}