package com.koren.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ScaffoldState(
    val title: String = "Koren",
    val isBackVisible: Boolean = true,
    val isTopBarVisible: Boolean = true,
    val isBottomBarVisible: Boolean = true,
    val customBackAction: (() -> Unit)? = null
)

class ScaffoldStateProvider {
    
    private val scaffoldState = MutableStateFlow(ScaffoldState())
    
    fun setScaffoldState(state: ScaffoldState) {
        scaffoldState.value = state
    }
    
    fun getScaffoldState(): StateFlow<ScaffoldState> = scaffoldState
}

val LocalScaffoldStateProvider = compositionLocalOf { ScaffoldStateProvider() }