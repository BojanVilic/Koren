package com.koren.map.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat.checkSelfPermission
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
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.activity.LocationActivity
import com.koren.common.services.LocationService
import com.koren.common.services.UserSession
import com.koren.data.repository.ActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DefaultLocationService @Inject constructor(
    private val context: Context,
    private val activityRepository: ActivityRepository,
    private val userSession: UserSession,
    private val placesClient: PlacesClient
): LocationService {

    init {
        val placeFields: List<Place.Field> = listOf(Place.Field.DISPLAY_NAME)
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

//        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
//            placesClient.findCurrentPlace(request)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val response = task.result
//                        val place = response.placeLikelihoods.first()
//                        Timber.d("Place '${place.place.displayName}' has likelihood: ${place.likelihood}")
//                    } else {
//                        val exception = task.exception
//                        if (exception is ApiException) {
//                            Timber.d("Place not found: ${exception.statusCode}")
//                        }
//                    }
//                }
//        }
    }

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5.seconds.inWholeMilliseconds)
        .build()

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getLocation(result: (Result<Location>) -> Unit) {
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) result(Result.failure(IllegalStateException("Location permission not granted")))
        fusedLocationClient.lastLocation
            .addOnSuccessListener { result(Result.success(it)) }
            .addOnFailureListener { result(Result.failure(it)) }
    }

    override fun requestLocationUpdates(): Flow<Location> = callbackFlow {
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
    }
        .onEach {
            val userData = userSession.currentUser.first()

            val locationActivity = LocationActivity(
                id = UUID.randomUUID().toString(),
                userId = userData.id,
                familyId = userData.familyId,
                createdAt = System.currentTimeMillis(),
                locationName = "Location"
            )

            activityRepository.insertNewActivity(locationActivity)
        }
        .flowOn(Dispatchers.Default)

    override fun isLocationPermissionGranted(): Boolean {
        return checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    override fun getPlaceSuggestions(query: String): Flow<List<SuggestionResponse>> = callbackFlow {
        val token = AutocompleteSessionToken.newInstance()

        val autocompleteRequest = FindAutocompletePredictionsRequest.builder()
            .setOrigin(LatLng(-33.8749937, 151.2041382))
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
}