package com.koren.chat.ui

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.koren.common.models.user.UserData
import com.koren.common.services.UserSession
import com.koren.common.util.StateViewModel
import com.koren.common.util.orUnknownError
import com.koren.domain.GetChatMessagesUseCase
import com.koren.domain.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val userSession: UserSession
) : StateViewModel<ChatEvent, ChatUiState, ChatSideEffect>() {

    override fun setInitialState(): ChatUiState = ChatUiState.Loading
    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    init {
        scope.launchMolecule(mode = RecompositionMode.ContextClock) {
            val user by userSession.currentUser.collectAsState(initial = UserData())
            val messages by getChatMessagesUseCase(user.familyId).collectAsState(initial = emptyList())

            if (user.familyId.isBlank()) {
                _uiState.update { ChatUiState.NoFamily }
                return@launchMolecule
            }

            when (val currentState = uiState.collectAsState().value) {
                is ChatUiState.Success -> {
                    _uiState.update {
                        currentState.copy(
                            messages = messages,
                            currentUser = user,
                            eventSink = ::handleEvent
                        )
                    }
                }
                else -> {
                    _uiState.update {
                        ChatUiState.Success(
                            messages = messages,
                            currentUser = user,
                            eventSink = ::handleEvent
                        )
                    }
                }
            }
        }
    }

    override fun handleEvent(event: ChatEvent) {
        withEventfulState<ChatUiState.Success> { currentState ->
            when (event) {
                is ChatEvent.MessageInputChanged -> _uiState.update { currentState.copy(messageInput = event.text) }
                is ChatEvent.SendMessageClicked -> sendMessage(currentState)
            }
        }
    }

    private fun sendMessage(currentState: ChatUiState.Success) {
        if (currentState.isSending || currentState.messageInput.isBlank()) return

        _uiState.update { currentState.copy(isSending = true) }

        viewModelScope.launch(Dispatchers.IO) {
            sendChatMessageUseCase(currentState.messageInput)
                .onSuccess { _uiState.update { currentState.copy(messageInput = "", isSending = false) } }
                .onFailure { error ->
                    Timber.e(error, "Failed to send message")
                    _uiState.update { currentState.copy(isSending = false) }
                    _sideEffects.emitSuspended(ChatSideEffect.ShowErrorSnackbar(error.message.orUnknownError()))
                }
        }
    }
} 