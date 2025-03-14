package com.koren.activity.ui

import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    userSession: UserSession
): StateViewModel<ActivityEvent, ActivityUiState, ActivitySideEffect>() {

    override fun setInitialState(): ActivityUiState = ActivityUiState.Loading

    init {
        combine(
            activityRepository.getLocationActivities(),
            userSession.currentUser
        ) { activities, user ->
            _uiState.update {
                if (user.familyId.isEmpty()) ActivityUiState.NoFamily
                else ActivityUiState.Shown(
                    activities = activities,
                    eventSink = ::handleEvent
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handleEvent(event: ActivityEvent) {
        withEventfulState<ActivityUiState.Shown> { currentState ->
            when (event) {
                is ActivityEvent.NavigateToCalendar -> _sideEffects.emitSuspended(ActivitySideEffect.NavigateToCalendar)
                is ActivityEvent.FetchMoreActivities -> fetchMoreActivities(currentState)
            }
        }
    }

    private fun fetchMoreActivities(currentState: ActivityUiState.Shown) {
        val lastTime = currentState.activities.lastOrNull()?.createdAt ?: return
        _uiState.update { currentState.copy(fetchingMore = true) }
        activityRepository.getMoreLocationActivities(lastTime)
            .onEach { (newItems, hasMore) ->
                delay(3000L)
                _uiState.update {
                    currentState.copy(
                        activities = currentState.activities + newItems,
                        fetchingMore = false,
                        canFetchMore = hasMore
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}