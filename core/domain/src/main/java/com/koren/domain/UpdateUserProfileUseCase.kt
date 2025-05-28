package com.koren.domain

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.user.UserData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) {
    suspend operator fun invoke(userData: UserData, newProfilePicture: Uri? = null): Result<Unit> {
        if (newProfilePicture != null) {
            uploadProfilePictureUseCase(userData.id, newProfilePicture)
                .onFailure { return Result.failure(it) }
                .onSuccess {
                    try {
                        firebaseDatabase.reference.child("users/${userData.id}")
                            .setValue(userData.copy(profilePictureUrl = it))
                            .await()
                        return Result.success(Unit)
                    } catch (e: Exception) {
                        return Result.failure(e)
                    }
                }
        } else {
            try {
                firebaseDatabase.reference.child("users/${userData.id}")
                    .setValue(userData)
                    .await()
                return Result.success(Unit)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
        return Result.failure(Exception("An unexpected error occurred"))
    }
}