package com.koren.domain

import android.net.Uri
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class UploadProfilePictureUseCase @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend operator fun invoke(userId: String, pictureUri: Uri): Result<String> {
        return try {
            val storageRef = firebaseStorage.getReference("user_profile_pictures").child("${userId}.jpg")
            storageRef.putFile(pictureUri).await()

            val profilePictureUrl = storageRef.downloadUrl.await().toString()
            val updates = mapOf<String, Any>(
                "profilePictureUrl" to profilePictureUrl
            )
            firebaseDatabase.getReference("users/$userId").updateChildren(updates).await()
            Result.success(profilePictureUrl)
        } catch (e: StorageException) {
            println("Storage Exception: ${e.message}")
            Result.failure(UploadProfilePictureException("Error uploading to storage: ${e.message}", e))
        } catch (e: DatabaseException) {
            println("Database Exception: ${e.message}")
            Result.failure(UploadProfilePictureException("Error writing to database: ${e.message}", e))
        } catch (e: IOException){
            println("IO Exception: ${e.message}")
            Result.failure(UploadProfilePictureException("Error reading the file: ${e.message}", e))
        } catch (e: Exception) {
            println("Generic Exception: ${e.message}")
            Result.failure(UploadProfilePictureException("An unexpected error occurred: ${e.message}", e))
        }
    }
}

class UploadProfilePictureException(message: String, cause: Throwable) : Exception(message, cause)