package com.koren.chat.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.koren.chat.ui.attachments_list.AttachmentsListDestination
import com.koren.chat.ui.attachments_list.AttachmentsListScreen
import com.koren.chat.ui.chat.ChatDestination
import com.koren.chat.ui.chat.ChatScreen
import com.koren.chat.ui.chat.more_options.MoreOptionsDestination
import com.koren.chat.ui.chat.more_options.MoreOptionsScreen
import com.koren.chat.ui.full_screen_image.FullScreenImageDestination
import com.koren.chat.ui.full_screen_image.FullScreenImageScreen
import com.koren.chat.ui.full_screen_video.FullScreenVideoDestination
import com.koren.chat.ui.full_screen_video.FullScreenVideoScreen
import com.koren.designsystem.components.bottom_sheet.bottomSheet
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
                onShowSnackbar = onShowSnackbar,
                onNavigateToImageAttachment = { messageId ->
                    navController.navigate(AttachmentsListDestination(messageId))
                },
                onNavigateToFullScreenImage = { mediaUrl ->
                    navController.navigate(FullScreenImageDestination(mediaUrl))
                },
                onNavigateToFullScreenVideo = { mediaUrl ->
                    navController.navigate(FullScreenVideoDestination(mediaUrl))
                },
                onNavigateToMoreOptions = { messageId ->
                    navController.navigate(MoreOptionsDestination(messageId))
                }
            )
        }
        composable<AttachmentsListDestination> {
            AttachmentsListScreen(
                onShowSnackbar = onShowSnackbar,
                navigateToFullScreenImage = { mediaUrl ->
                    navController.navigate(FullScreenImageDestination(mediaUrl))
                }
            )
        }
        composable<FullScreenImageDestination> {
            FullScreenImageScreen(
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<FullScreenVideoDestination> {
            FullScreenVideoScreen(
                onShowSnackbar = onShowSnackbar
            )
        }
        bottomSheet<MoreOptionsDestination> {
            MoreOptionsScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}