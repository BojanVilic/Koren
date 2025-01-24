package com.koren.domain

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.koren.common.models.family.SavedLocation
import com.koren.common.services.UserSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetFamilyLocations @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val userSession: UserSession
) {
    operator fun invoke(): Flow<List<SavedLocation>> = callbackFlow {
        val user = userSession.currentUser.first()

        val ref = firebaseDatabase.reference.child("families/${user.familyId}/savedLocations")
        val listener = ref
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val locations = snapshot.children.mapNotNull { dataSnapshot ->
                        dataSnapshot.getValue<SavedLocation>()
                    }
                    trySend(locations).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose { ref.removeEventListener(listener) }

    }
}