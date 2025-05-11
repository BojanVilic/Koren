package com.koren.chat.ui.full_screen_video

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.koren.common.util.MoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullScreenVideoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MoleculeViewModel<FullScreenVideoUiEvent, FullScreenVideoUiState, FullScreenVideoUiSideEffect>() {

    private val videoUrl = savedStateHandle.toRoute<FullScreenVideoDestination>().videoUrl

    override fun setInitialState(): FullScreenVideoUiState = FullScreenVideoUiState.Loading

    @Composable
    override fun produceState(): FullScreenVideoUiState {

        return FullScreenVideoUiState.Shown(
            videoUrl = videoUrl
        ) { event ->
            when (event) {
                else -> Unit
            }
        }
    }
}