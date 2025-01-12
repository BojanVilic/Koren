package com.koren.common.services

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationService {
    suspend fun getLocation(result: (Result<Location>) -> Unit)
    fun requestLocationUpdates(): Flow<Location>
    fun isLocationPermissionGranted(): Boolean
}