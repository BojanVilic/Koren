package com.koren.common.services

import android.location.Location
import com.koren.common.models.suggestion.SuggestionResponse
import kotlinx.coroutines.flow.Flow

interface LocationService {
    suspend fun updateLocationOnce(): Location
    fun requestLocationUpdates(frequency: Int): Flow<Location>
    fun isLocationPermissionGranted(): Boolean

    suspend fun getPlaceSuggestions(query: String): List<SuggestionResponse>
    suspend fun getPlaceDetails(placeId: String): SuggestionResponse?

    suspend fun getLocationName(location: Location): String
}