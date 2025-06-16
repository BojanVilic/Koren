package com.koren.domain

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.koren.common.models.user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDistanceBetweenTwoUsersUseCase @Inject constructor() {
    suspend operator fun invoke(
        currentUser: UserData,
        targetUser: UserData
    ): Int {
        val currentUserLat = currentUser.lastLocation?.latitude ?: 0.0
        val currentUserLon = currentUser.lastLocation?.longitude ?: 0.0
        val memberLat = targetUser.lastLocation?.latitude ?: 0.0
        val memberLon = targetUser.lastLocation?.longitude ?: 0.0


        return withContext(Dispatchers.IO) {
            SphericalUtil.computeDistanceBetween(
                LatLng(currentUserLat, currentUserLon),
                LatLng(memberLat, memberLon)
            ).toLong().toInt()
        }
    }
}