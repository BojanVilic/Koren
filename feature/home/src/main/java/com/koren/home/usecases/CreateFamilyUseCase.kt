package com.koren.home.usecases

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.Family
import com.koren.common.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class CreateFamilyUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val uploadFamilyPictureUseCase: UploadFamilyPictureUseCase
) {

    operator fun invoke(familyName: String, familyPortraitPath: Uri? = null) = flow {
        emit(Resource.Loading())

        val familyId = UUID.randomUUID().toString()
        val familyPortraitUrl = familyPortraitPath?.let { uploadFamilyPictureUseCase(familyId, it) }

        val family = Family(
            id = familyId,
            name = familyName,
            members = emptyList(),
            familyPortrait = familyPortraitUrl ?: ""
        )

        firebaseDatabase.getReference("families").child(family.id).setValue(family).await()
        emit(Resource.Success(Unit))
    }.catch { e ->
        emit(Resource.Error(e))
    }.flowOn(Dispatchers.IO)

}