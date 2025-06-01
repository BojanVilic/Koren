package com.koren.activity.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import com.koren.common.models.activity.UserLocationActivity
import com.koren.common.services.UserSession
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val userSession: UserSession
): MoleculeViewModel<ActivityEvent, ActivityUiState, ActivitySideEffect>() {

    override fun setInitialState(): ActivityUiState = ActivityUiState.Loading

    @Composable
    override fun produceState(): ActivityUiState {
        val remoteActivities by activityRepository.getLocationActivities().collectAsState(initial = emptyList())
        val user by userSession.currentUser.collectAsState(initial = null)

        if (user?.familyId.isNullOrEmpty()) return ActivityUiState.NoFamily

        val fetchingMore = remember { mutableStateOf(false) }
        val canFetchMore = remember { mutableStateOf(true) }
        val activities = remember(remoteActivities) { mutableStateOf(remoteActivities) }

        return ActivityUiState.Shown(
            activities = activities.value,
            fetchingMore = fetchingMore.value,
            canFetchMore = canFetchMore.value
        ) { event ->
            when (event) {
                is ActivityEvent.NavigateToCalendar -> _sideEffects.emitSuspended(ActivitySideEffect.NavigateToCalendar)
                is ActivityEvent.FetchMoreActivities -> fetchMoreActivities(activities, fetchingMore, canFetchMore)
                is ActivityEvent.AnswersClicked -> sendSideEffect(ActivitySideEffect.NavigateToAnswers)
            }
        }
    }

    private fun fetchMoreActivities(
        activities: MutableState<List<UserLocationActivity>>,
        fetchingMore: MutableState<Boolean>,
        canFetchMore: MutableState<Boolean>
    ) {
        val lastTime = activities.value.lastOrNull()?.createdAt ?: return
        fetchingMore.value = true
        activityRepository.getMoreLocationActivities(lastTime)
            .onEach { (newItems, hasMore) ->
                activities.value = activities.value + newItems
                fetchingMore.value = false
                canFetchMore.value = hasMore
            }
            .launchIn(viewModelScope)
    }
}