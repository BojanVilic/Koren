package com.koren.map.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class DefaultLocationService @Inject constructor(
    private val context: Context,
    private val activityRepository: ActivityRepository,
    private val userSession: UserSession
): LocationService {

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30.minutes.inWholeMilliseconds)
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
}