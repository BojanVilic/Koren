package com.koren.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface UiEvent
interface UiState
interface UiSideEffect
interface EventHandler<E: UiEvent> : UiState {
    val eventSink: (E) -> Unit
}

abstract class MoleculeViewModel<Event: UiEvent, State: UiState, SideEffect: UiSideEffect> : ViewModel() {
    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    protected val _sideEffects: MutableSharedFlow<SideEffect> = MutableSharedFlow()
    val sideEffects: SharedFlow<SideEffect> = _sideEffects.asSharedFlow()

    protected abstract fun setInitialState(): State

    private val initialState: State by lazy { setInitialState() }

    val uiState: StateFlow<State> by lazy {
        scope.launchMolecule(mode = RecompositionMode.ContextClock) {
            produceState()
        }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = initialState
            )
    }

    @Composable
    protected abstract fun produceState(): State

    protected fun MutableSharedFlow<SideEffect>.emitSuspended(effect: SideEffect) {
        viewModelScope.launch {
            emit(effect)
        }
    }
}


abstract class StateViewModel<Event: UiEvent, State: UiState, SideEffect: UiSideEffect> : ViewModel() {
    protected abstract fun setInitialState(): State
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

@Composable
fun <Effect : UiSideEffect> CollectSideEffects(
    viewModel: MoleculeViewModel<*, *, Effect>,
    handleSideEffect: suspend (Effect) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            handleSideEffect(effect)
        }
    }
}