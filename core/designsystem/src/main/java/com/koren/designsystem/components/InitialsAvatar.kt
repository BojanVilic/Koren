package com.koren.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

@Composable
fun InitialsAvatar(
    modifier: Modifier = Modifier,
    name: String,
    imageUrl: String?,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val initials = remember(name) { getInitials(name) }

    SubcomposeAsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .build(),
        contentDescription = "$name's avatar",
        contentScale = ContentScale.Crop
    ) {
        val painterState by painter.state.collectAsState()
        if (painterState is AsyncImagePainter.State.Loading || painterState is AsyncImagePainter.State.Error) {
            Box(
                modifier = modifier.background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = initials,
                    color = textColor,
                    style = MaterialTheme.typography.headlineSmall,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 8.sp,
                        maxFontSize = 36.sp,
                        stepSize = 2.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}

fun getInitials(name: String): String {
    if (name.isBlank()) return ""
    val parts = name.split(" ")
    return when {
        parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
        parts.isNotEmpty() -> parts.first().first().uppercase()
        else -> ""
    }
}

@ThemePreview
@Composable
fun InitialsAvatarPreview() {
    KorenTheme {
        InitialsAvatar(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp),
            name = "Dragan Torbica",
            imageUrl = null,
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}