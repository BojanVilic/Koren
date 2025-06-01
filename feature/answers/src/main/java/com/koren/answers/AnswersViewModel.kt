package com.koren.answers

import androidx.compose.runtime.Composable
import com.koren.common.util.MoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnswersViewModel @Inject constructor(

) : MoleculeViewModel<AnswersUiEvent, AnswersUiState, AnswersUiSideEffect>() {

    override fun setInitialState(): AnswersUiState = AnswersUiState.Loading

    @Composable
    override fun produceState(): AnswersUiState {

        return AnswersUiState.Shown { event ->
            when (event) {
                else -> Unit
            }
        }
    }
}