package com.koren.chat.ui.attachments_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
data class AttachmentsListDestination(val messageId: String)

@Composable
fun AttachmentsListScreen(
    viewModel: AttachmentsListViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit,
    navigateToFullScreenImage: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val mediaUrls = when (uiState) {
        is AttachmentsListUiState.Loading -> emptyList()
        is AttachmentsListUiState.Shown -> (uiState as AttachmentsListUiState.Shown).mediaUrls
    }

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            title = "Attachments (${mediaUrls.size})",
            isTopBarVisible = true,
            isBottomBarVisible = false
        )
    )

    CollectSideEffects(viewModel) { sideEffect ->
        when (sideEffect) {
            is AttachmentsListUiSideEffect.ShowError -> onShowSnackbar(sideEffect.message)
            is AttachmentsListUiSideEffect.NavigateToFullScreenImage -> navigateToFullScreenImage(sideEffect.mediaUrl)
        }
    }

    AttachmentsListScreenContent(uiState)
}

@Composable
private fun AttachmentsListScreenContent(uiState: AttachmentsListUiState) {
    when (uiState) {
        is AttachmentsListUiState.Loading -> LoadingContent()
        is AttachmentsListUiState.Shown -> AttachmentsListScreenShownContent(uiState)
    }
}

@Composable
private fun AttachmentsListScreenShownContent(
    uiState: AttachmentsListUiState.Shown
) {
    val mediaUrls = uiState.mediaUrls

    if (mediaUrls.isEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "No images available",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    } else {
        Column {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mediaUrls) { url ->
                    AsyncImage(
                        modifier = Modifier
                            .size(height = 200.dp, width = 120.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                uiState.eventSink(AttachmentsListUiEvent.OnImageClick(url))
                            },
                        model = ImageRequest.Builder(LocalContext.current)
                            .crossfade(true)
                            .data(url)
                            .build(),
                        contentDescription = null,
                        placeholder = coilPlaceholder(KorenIcons.NoImage),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@ThemePreview
@Composable
private fun AttachmentsListScreenPreview() {
    KorenTheme {
        AttachmentsListScreenContent(
            AttachmentsListUiState.Shown(
                mediaUrls = listOf(
                    "https://example.com/image1.jpg",
                    "https://example.com/image2.jpg",
                    "https://example.com/image3.jpg",
                ),
                eventSink = {}
            )
        )
    }
}