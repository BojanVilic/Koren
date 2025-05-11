package com.koren.chat.ui.full_screen_video

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
data class FullScreenVideoDestination(val videoUrl: String)

@Composable
fun FullScreenVideoScreen(
    viewModel: FullScreenVideoViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {
    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            title = "",
            isTopBarVisible = true,
            isBottomBarVisible = false
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel) { sideEffect ->
        when (sideEffect) {
            else -> Unit
        }
    }

    FullScreenVideoScreenContent(uiState)
}

@Composable
private fun FullScreenVideoScreenContent(uiState: FullScreenVideoUiState) {
    when (uiState) {
        is FullScreenVideoUiState.Loading -> LoadingContent()
        is FullScreenVideoUiState.Shown -> FullScreenVideoScreenShownContent(uiState)
    }
}

@Composable
private fun FullScreenVideoScreenShownContent(uiState: FullScreenVideoUiState.Shown) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uiState.videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@ThemePreview
@Composable
private fun FullScreenVideoScreenPreview() {
    KorenTheme {
        FullScreenVideoScreenContent(
            FullScreenVideoUiState.Shown(
                videoUrl = "https://www.example.com/video.mp4",
                eventSink = {}
            )
        )
    }
}