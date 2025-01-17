package com.koren.common.services

import android.location.Location
import com.koren.common.models.suggestion.SuggestionResponse
import kotlinx.coroutines.flow.Flow

interface LocationService {
    suspend fun getLocation(result: (Result<Location>) -> Unit)
    fun requestLocationUpdates(): Flow<Location>
    fun isLocationPermissionGranted(): Boolean

    fun getPlaceSuggestions(query: String): Flow<List<SuggestionResponse>>
}