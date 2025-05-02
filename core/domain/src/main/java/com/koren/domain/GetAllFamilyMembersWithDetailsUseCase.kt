package com.koren.domain

import com.koren.common.models.family.CallHomeRequestStatus
import com.koren.common.models.family.FamilyMemberUserData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllFamilyMembersWithDetailsUseCase @Inject constructor(
    private val getFamilyUseCase: GetFamilyUseCase,
    private val getAllFamilyMembersUseCase: GetAllFamilyMembersUseCase,
    private val getDistanceToHomeUseCase: GetDistanceToHomeUseCase
) {
    operator fun invoke(): Flow<List<FamilyMemberUserData>> {
        return combine(
            getFamilyUseCase.getFamilyFlow(),
            getAllFamilyMembersUseCase()
        ) { family, members ->
            members.map { member ->
                val isGoingHome = family?.callHomeRequests?.get(member.id)?.status == CallHomeRequestStatus.ACCEPTED
                member to isGoingHome
            }
        }.flatMapLatest { membersWithStatus ->
            if (membersWithStatus.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val distanceFlows = membersWithStatus.mapNotNull { (member, isGoingHome) ->
                if (isGoingHome) {
                    getDistanceToHomeUseCase(member.id).map { distance ->
                        member.id to distance
                    }
                } else null
            }

            val combinedFlow = if (distanceFlows.isEmpty()) {
                flowOf(emptyMap())
            } else {
                combine(distanceFlows) { distancesArray ->
                    distancesArray.toMap()
                }
            }

            combinedFlow.map { distanceMap ->
                membersWithStatus.map { (member, isGoingHome) ->
                    FamilyMemberUserData(
                        userData = member,
                        distance = if (isGoingHome) distanceMap[member.id] ?: 0 else 0,
                        goingHome = isGoingHome
                    )
                }
            }
        }
    }
}