package com.koren.domain

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.koren.common.models.user.UserData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDistanceBetweenTwoUsersUseCase @Inject constructor() {
    operator fun invoke(
        currentUser: UserData,
        familyMemberDetails: UserData
    ): Int {
        val currentUserLat = currentUser.lastLocation?.latitude ?: 0.0
        val currentUserLon = currentUser.lastLocation?.longitude ?: 0.0
        val memberLat = familyMemberDetails.lastLocation?.latitude ?: 0.0
        val memberLon = familyMemberDetails.lastLocation?.longitude ?: 0.0

        return SphericalUtil.computeDistanceBetween(
            LatLng(currentUserLat, currentUserLon),
            LatLng(memberLat, memberLon)
        ).toLong().toInt()
    }
}