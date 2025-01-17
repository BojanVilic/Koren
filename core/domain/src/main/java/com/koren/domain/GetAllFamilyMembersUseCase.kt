package com.koren.domain

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.family.Family
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetAllFamilyMembersUseCase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    operator fun invoke(): Flow<List<UserData>> = callbackFlow {
        val familyId = userSession.currentUser.first().familyId

        val memberIds = firebaseDatabase.getReference("families/$familyId")
            .get()
            .await()
            .getValue<Family>()?.members

        val query = firebaseDatabase.getReference("users")

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue<UserData>()
                }.filter { it.id in (memberIds ?: emptyList()) }
                trySend(users).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }.flowOn(Dispatchers.Default)
}