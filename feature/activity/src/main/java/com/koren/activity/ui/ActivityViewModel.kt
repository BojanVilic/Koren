package com.koren.activity.ui

import com.koren.common.util.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(

): StateViewModel<ActivityEvent, ActivityUiState, ActivitySideEffect>() {

    override fun setInitialState(): ActivityUiState = ActivityUiState.Loading

    override fun handleEvent(event: ActivityEvent) {
        withEventfulState<ActivityUiState.Shown> {
            when (event) {
                ActivityEvent.OnClick -> Unit
            }
        }
    }
}