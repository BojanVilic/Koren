package com.koren.chat.ui.full_screen_image

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.components.coilPlaceholder
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.NoImage
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
data class FullScreenImageDestination(val mediaUrl: String)

@Composable
fun FullScreenImageScreen(
    viewModel: FullScreenImageViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {
    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isBottomBarVisible = false
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel) { sideEffect ->
        when (sideEffect) {
            else -> Unit
        }
    }

    FullScreenImageScreenContent(uiState)
}

@Composable
private fun FullScreenImageScreenContent(uiState: FullScreenImageUiState) {
    when (uiState) {
        is FullScreenImageUiState.Loading -> LoadingContent()
        is FullScreenImageUiState.Shown -> FullScreenImageScreenShownContent(uiState)
    }
}

@Composable
private fun FullScreenImageScreenShownContent(
    uiState: FullScreenImageUiState.Shown
) {
    var zoom by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }


    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        zoom = (zoom * zoomChange).coerceIn(1f, 3f)
        val newOffset = offset + offsetChange.times(zoom)

        val maxX = (size.width * (zoom - 1) / 2f)
        val maxY = (size.height * (zoom - 1) / 2f)

        offset = Offset(
            newOffset.x.coerceIn(-maxX, maxX),
            newOffset.y.coerceIn(-maxY, maxY)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (zoom > 1f) {
                            zoom = 1f
                            offset = Offset.Zero
                        } else
                            zoom *= 2f
                    }
                )
            }
            .graphicsLayer(
                scaleX = zoom,
                scaleY = zoom,
                translationX = offset.x,
                translationY = offset.y
            )
            .onSizeChanged {
                size = it
            }
            .transformable(state = state),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium),
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(true)
                .data(uiState.mediaUrl)
                .build(),
            contentDescription = null,
            placeholder = coilPlaceholder(KorenIcons.NoImage),
            contentScale = ContentScale.FillWidth
        )
    }
}

@ThemePreview
@Composable
private fun FullScreenImageScreenPreview() {
    KorenTheme {
        FullScreenImageScreenContent(
            FullScreenImageUiState.Shown(
                mediaUrl = "https://example.com/image.jpg",
                eventSink = {}
            )
        )
    }
}