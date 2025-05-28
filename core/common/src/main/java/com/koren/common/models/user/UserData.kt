package com.koren.common.models.user

import com.koren.common.models.family.FamilyRole
import com.koren.common.util.Constants.DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS

data class UserData(
    val id: String = "",
    val familyId: String = "",
    val displayName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val familyRole: FamilyRole = FamilyRole.NONE,
    val lastLocation: UserLocation? = null,
    val lastActivityId: String = "",
    val fcmToken: String = "",
    val locationUpdateFrequencyInMins: Int = DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS
)