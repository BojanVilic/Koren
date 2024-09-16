package com.koren.home.usecases

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.Family
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CreateFamilyUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val uploadFamilyPictureUseCase: UploadFamilyPictureUseCase
) {

    suspend operator fun invoke(familyName: String, familyPortraitPath: Uri? = null) {
        withContext(Dispatchers.IO) {
            val familyId = UUID.randomUUID().toString()

            val familyPortraitUrl = familyPortraitPath?.let { uploadFamilyPictureUseCase(familyId, it) }

            val family = Family(
                id = familyId,
                name = familyName,
                members = emptyList(),
                familyPortrait = familyPortraitUrl?: ""
            )

            firebaseDatabase.getReference("families").child(family.id).setValue(family)
        }
    }
}