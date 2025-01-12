package com.koren.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface UiEvent
interface UiState
interface UiSideEffect
interface EventHandler<E: UiEvent> : UiState {
    val eventSink: (E) -> Unit
}

abstract class StateViewModel<Event: UiEvent, State: UiState, SideEffect: UiSideEffect> : ViewModel() {
    abstract fun setInitialState(): State
    protected abstract fun handleEvent(event: Event)

    private val initialState: State by lazy { setInitialState() }

    protected val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState

    protected val _sideEffects: MutableSharedFlow<SideEffect> = MutableSharedFlow()
    val sideEffects: SharedFlow<SideEffect> = _sideEffects.asSharedFlow()

    protected fun MutableSharedFlow<SideEffect>.emitSuspended(effect: SideEffect) {
        viewModelScope.launch {
            emit(effect)
        }
    }

    protected inline fun <reified T : EventHandler<Event>> withEventfulState(action: (T) -> Unit) {
        val currentState = _uiState.value
        if (currentState is T) {
            action(currentState)
        }
    }
}

@Composable
fun <Effect : UiSideEffect> CollectSideEffects(
    viewModel: StateViewModel<*, *, Effect>,
    handleSideEffect: suspend (Effect) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            handleSideEffect(effect)
        }
    }
}