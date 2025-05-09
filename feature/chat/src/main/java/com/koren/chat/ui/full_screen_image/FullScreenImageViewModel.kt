package com.koren.chat.ui.full_screen_image

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.koren.common.util.MoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullScreenImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MoleculeViewModel<FullScreenImageUiEvent, FullScreenImageUiState, FullScreenImageUiSideEffect>() {

    private val mediaUrl = savedStateHandle.toRoute<FullScreenImageDestination>().mediaUrl

    override fun setInitialState(): FullScreenImageUiState = FullScreenImageUiState.Loading

    @Composable
    override fun produceState(): FullScreenImageUiState {

        return FullScreenImageUiState.Shown(
            mediaUrl = mediaUrl
        ) { event ->
            when (event) {
                else -> Unit
            }
        }
    }
}