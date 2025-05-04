package com.koren.chat.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.chat.ui.ChatDestination
import com.koren.chat.ui.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
object ChatGraph

fun NavGraphBuilder.chatScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (message: String) -> Unit,
) {
    navigation<ChatGraph>(
        startDestination = ChatDestination
    ) {
        composable<ChatDestination> {
            ChatScreen(
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}