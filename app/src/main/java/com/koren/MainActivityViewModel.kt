package com.koren

import androidx.lifecycle.viewModelScope
import com.koren.common.services.UserNotLoggedInException
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.common.util.orUnknownError
import com.koren.navigation.MainActivityUiEvent
import com.koren.navigation.MainActivityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userSession: UserSession
) : StateViewModel<MainActivityUiEvent, MainActivityUiState, Nothing>() {

    override fun setInitialState(): MainActivityUiState = MainActivityUiState.Loading

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!userSession.isLoggedIn) _uiState.value = MainActivityUiState.LoggedOut
            else {
                userSession.currentUser.collect { userData ->
                    try {
                        _uiState.update {
                            MainActivityUiState.Success(
                                userData = userData,
                                eventSink = ::handleEvent
                            )
                        }
                    }
                    catch (e: UserNotLoggedInException) {
                        _uiState.value = MainActivityUiState.LoggedOut
                    }
                    catch (e: Exception) {
                        _uiState.value = MainActivityUiState.Error(e.message.orUnknownError())
                    }
                }
            }
        }
    }

    override fun handleEvent(event: MainActivityUiEvent) {
        withEventfulState<MainActivityUiState.Success> { currentState ->
            when (event) {
                else -> Unit
            }
        }
    }
}