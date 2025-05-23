package com.koren.domain

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.family.Family
import com.koren.common.models.family.LocationIcon
import com.koren.common.models.family.SavedLocation
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.user.UserLocation
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class CreateFamilyUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val uploadFamilyPictureUseCase: UploadFamilyPictureUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val userSession: UserSession
) {
    suspend operator fun invoke(
        familyName: String,
        familyPortraitPath: Uri? = null,
        homeAddress: SuggestionResponse
    ) {
        val familyId = UUID.randomUUID().toString()
        val familyPortraitUrl = familyPortraitPath?.let { uploadFamilyPictureUseCase(familyId, it) }
        val userId = userSession.currentUser.first().id
        val members = listOf(userId)

        val family = Family(
            id = familyId,
            name = familyName,
            members = members,
            familyPortrait = familyPortraitUrl ?: "",
            homeLat = homeAddress.latitude,
            homeLng = homeAddress.longitude
        )

        firebaseDatabase.getReference("users").keepSynced(true)

        firebaseDatabase.getReference("families").child(family.id).setValue(family).await()
        firebaseDatabase.getReference("users").child(userId).child("familyId").setValue(family.id).await()
        saveLocationUseCase(
            SavedLocation(
                id = UUID.randomUUID().toString(),
                name = "Home",
                address = homeAddress.primaryText + ", " + homeAddress.secondaryText,
                latitude = homeAddress.latitude,
                longitude = homeAddress.longitude,
                iconName = LocationIcon.HOUSE.name
            )
        )
    }
}