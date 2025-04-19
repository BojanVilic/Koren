package com.koren.chat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.koren.chat.ui.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
object ChatDestination

fun NavGraphBuilder.chatScreen(
    navController: NavController,
    onShowSnackbar: suspend (String) -> Unit
) {
    composable<ChatDestination> {
        ChatScreen(
            onShowSnackbar = onShowSnackbar
        )
    }
} 