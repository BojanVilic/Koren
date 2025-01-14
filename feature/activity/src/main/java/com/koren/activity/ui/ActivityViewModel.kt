package com.koren.activity.ui

import androidx.lifecycle.viewModelScope
import com.koren.common.util.StateViewModel
import com.koren.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
): StateViewModel<ActivityEvent, ActivityUiState, ActivitySideEffect>() {

    override fun setInitialState(): ActivityUiState = ActivityUiState.Loading

    init {
        viewModelScope.launch {
            activityRepository.getActivities().collect { activities ->
                _uiState.update { ActivityUiState.Shown(
                    activities = activities,
                    eventSink = ::handleEvent
                ) }
            }
        }
    }

    override fun handleEvent(event: ActivityEvent) {
        withEventfulState<ActivityUiState.Shown> {
            when (event) {
                ActivityEvent.OnClick -> Unit
            }
        }
    }
}