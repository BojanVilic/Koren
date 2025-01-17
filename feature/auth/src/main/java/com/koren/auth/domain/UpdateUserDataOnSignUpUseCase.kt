package com.koren.auth.domain

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.koren.common.models.user.UserData
import com.koren.domain.UploadProfilePictureUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UpdateUserDataOnSignUpUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) {
    suspend operator fun invoke(
        displayName: String,
        imageUri: Uri?
    ) {
        val userId = firebaseAuth.currentUser?.uid ?: ""
        val email = firebaseAuth.currentUser?.email ?: ""

        if (imageUri != null) uploadProfilePictureUseCase(userId, imageUri)

        val currentUserData = firebaseDatabase.getReference("users")
            .child(userId)
            .get()
            .await()
            .getValue<UserData>()

        Firebase.database.getReference("users")
            .child(userId)
            .setValue(
                currentUserData?.copy(
                    id = userId,
                    email = email,
                    displayName = displayName
                )?: UserData(
                    id = userId,
                    email = email,
                    displayName = displayName
                )
            )
    }
}