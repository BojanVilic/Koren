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

interface Event
interface UiState
interface SideEffect
interface EventHandler<E: Event> : UiState {
    val eventSink: (E) -> Unit
}

abstract class BaseViewModel<E: Event, S: UiState, Effect: SideEffect> : ViewModel() {
    abstract fun setInitialState(): S
    protected abstract fun handleEvent(event: E)

    private val initialState: S by lazy { setInitialState() }

    protected val _uiState: MutableStateFlow<S> = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState

    protected val _sideEffects: MutableSharedFlow<Effect> = MutableSharedFlow()
    val sideEffects: SharedFlow<Effect> = _sideEffects.asSharedFlow()

    protected fun MutableSharedFlow<Effect>.emitSuspended(effect: Effect) {
        viewModelScope.launch {
            emit(effect)
        }
    }

    protected inline fun <reified T : EventHandler<E>> withEventfulState(action: (T) -> Unit) {
        val currentState = _uiState.value
        if (currentState is T) {
            action(currentState)
        }
    }
}

@Composable
fun <Effect : SideEffect> CollectSideEffects(
    viewModel: BaseViewModel<*, *, Effect>,
    handleSideEffect: suspend (Effect) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            handleSideEffect(effect)
        }
    }
}