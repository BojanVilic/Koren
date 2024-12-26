package com.koren.onboarding.usecases

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UploadFamilyPictureUseCase @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {
    suspend operator fun invoke(familyId: String, pictureUri: Uri): String {
        val storageRef = firebaseStorage.getReference("family_profile_pictures").child("${familyId}.jpg")
        storageRef.putFile(pictureUri).await()
        return storageRef.downloadUrl.await().toString()
    }
}