package com.koren.onboarding.usecases

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.Family
import com.koren.common.services.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class CreateFamilyUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val uploadFamilyPictureUseCase: UploadFamilyPictureUseCase,
    private val userSession: UserSession
) {

    suspend operator fun invoke(familyName: String, familyPortraitPath: Uri? = null) {
        val familyId = UUID.randomUUID().toString()
        val familyPortraitUrl = familyPortraitPath?.let { uploadFamilyPictureUseCase(familyId, it) }
        val userId = userSession.currentUser.first().id
        val members = listOf(userId)

        val family = Family(
            id = familyId,
            name = familyName,
            members = members,
            familyPortrait = familyPortraitUrl ?: ""
        )

        firebaseDatabase.getReference("users").keepSynced(true)

        firebaseDatabase.getReference("families").child(family.id).setValue(family).await()
        firebaseDatabase.getReference("users").child(userId).child("familyId").setValue(family.id).await()
    }
}