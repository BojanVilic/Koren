package com.koren.chat.ui.chat.more_options

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.koren.common.models.chat.ChatMessage
import com.koren.common.util.MoleculeViewModel
import com.koren.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreOptionsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository
) : MoleculeViewModel<MoreOptionsUiEvent, MoreOptionsUiState, MoreOptionsUiSideEffect>() {

    override fun setInitialState(): MoreOptionsUiState = MoreOptionsUiState.Loading

    @Composable
    override fun produceState(): MoreOptionsUiState {
        val messageId = savedStateHandle.toRoute<MoreOptionsDestination>().messageId
        var message by remember { mutableStateOf(ChatMessage()) }

        LaunchedEffect(Unit) {
            chatRepository.getMessageById(messageId)
                .onSuccess { message = it }
                .onFailure { _sideEffects.emitSuspended(MoreOptionsUiSideEffect.NavigateBack) }
        }

        return MoreOptionsUiState.Shown(
            message = message
        ) { event ->
            when (event) {
                is MoreOptionsUiEvent.DeleteMessage -> viewModelScope.launch {
                    chatRepository.deleteMessage(messageId)
                        .onSuccess { _sideEffects.emitSuspended(MoreOptionsUiSideEffect.NavigateBack) }
                }
                is MoreOptionsUiEvent.AddReaction -> addReactionToMessage(
                    messageId = messageId,
                    reaction = event.reaction,
                    onSuccess = { _sideEffects.emitSuspended(MoreOptionsUiSideEffect.NavigateBack) }
                )
            }
        }
    }

    private fun addReactionToMessage(
        messageId: String,
        reaction: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            chatRepository.addReactionToMessage(messageId, reaction)
                .onSuccess { onSuccess() }
        }
    }
}