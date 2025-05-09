package com.koren.chat.ui.full_screen_image

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.koren.common.util.MoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullScreenImageViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : MoleculeViewModel<FullScreenImageUiEvent, FullScreenImageUiState, FullScreenImageUiSideEffect>() {

    override fun setInitialState(): FullScreenImageUiState = FullScreenImageUiState.Loading

    @Composable
    override fun produceState(): FullScreenImageUiState {
        val mediaUrl = savedStateHandle.toRoute<FullScreenImageDestination>().mediaUrl

        return FullScreenImageUiState.Shown(
            mediaUrl = mediaUrl
        ) { event ->
            when (event) {
                else -> Unit
            }
        }
    }
}