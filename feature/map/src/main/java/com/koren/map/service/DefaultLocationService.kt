package com.koren.map.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat.checkSelfPermission
import coil.util.CoilUtils.result
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.maps.android.SphericalUtil
import com.koren.common.models.family.SavedLocation
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class DefaultLocationService @Inject constructor(
    private val context: Context,
    private val userSession: UserSession,
    private val placesClient: PlacesClient,
    private val firebaseDatabase: FirebaseDatabase,
): LocationService {

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30.seconds.inWholeMilliseconds)
        .build()

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun updateLocationOnce(): Location {
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) throw IllegalStateException("Location permission not granted")
        return fusedLocationClient.getCurrentLocation(locationRequest.priority, null)
            .await()
    }

    override fun requestLocationUpdates(): Flow<Location> = callbackFlow<Location> {
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        trySend(location)
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

            awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
        }
    }.flowOn(Dispatchers.Default)

    override fun isLocationPermissionGranted(): Boolean {
        return checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    override fun getPlaceSuggestions(query: String): Flow<List<SuggestionResponse>> = callbackFlow {
        val token = AutocompleteSessionToken.newInstance()

        val autocompleteRequest = FindAutocompletePredictionsRequest.builder()
            .setTypesFilter(listOf(PlaceTypes.ADDRESS))
            .setSessionToken(token)
            .setQuery(query)
            .build()

        try {
            val response: FindAutocompletePredictionsResponse = withContext(Dispatchers.IO) {
                placesClient.findAutocompletePredictions(autocompleteRequest).await()
            }
            val suggestionResponse = response.autocompletePredictions.map { prediction ->
                val place = placesClient.fetchPlace(FetchPlaceRequest.newInstance(prediction.placeId, listOf(Place.Field.LOCATION))).await()
                prediction.getPrimaryText(null).toString() to prediction.getSecondaryText(null).toString()

                SuggestionResponse(
                    primaryText = prediction.getPrimaryText(null).toString(),
                    secondaryText = prediction.getSecondaryText(null).toString(),
                    latitude = place.place.location?.latitude?: 0.0,
                    longitude = place.place.location?.longitude?: 0.0,
                )
            }

            trySend(suggestionResponse)
        } catch (exception: Exception) {
            if (exception is ApiException) {
                Timber.e("Place not found: ${exception.statusCode}")
            } else {
                Timber.e(exception, "Error fetching places")
            }
            trySend(emptyList())
        }

        awaitClose()
    }

    override suspend fun getLocationName(location: Location): String {
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) return ""
        val userData = userSession.currentUser.first()
        val savedLocations = firebaseDatabase.reference.child("families/${userData.familyId}/savedLocations")
            .get()
            .await()
            .children
            .mapNotNull { it.getValue<SavedLocation>() }

        val userLocation = LatLng(location.latitude, location.longitude)
        val closestLocation = savedLocations.minByOrNull {
            val savedLocation = LatLng(it.latitude, it.longitude)
            SphericalUtil.computeDistanceBetween(userLocation, savedLocation)
        }

        val distance = closestLocation?.let {
            SphericalUtil.computeDistanceBetween(userLocation, LatLng(it.latitude, it.longitude))
        } ?: Double.MAX_VALUE

        return if (distance > 300) {
//            val placeFields: List<Place.Field> = listOf(Place.Field.DISPLAY_NAME)
//            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
//
//            placesClient.findCurrentPlace(request)
//                .await()
//                .placeLikelihoods
//                .first()
//                .place
//                .displayName?: ""
            "Cut cost test location + ${UUID.randomUUID().toString().take(4)}"
        } else {
            closestLocation?.name ?: ""
        }
    }
}