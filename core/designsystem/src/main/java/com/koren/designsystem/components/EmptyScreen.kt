package com.koren.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.koren.designsystem.R
import com.koren.designsystem.theme.ThemePreview

@Composable
fun EmptyContent() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.nothing_to_see))
    val preloaderProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(64.dp))

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black)
        ) {
            LottieAnimation(
                modifier = Modifier.fillMaxHeight(0.3f),
                progress = { preloaderProgress },
                composition = composition
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.nothing_to_see_here),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@ThemePreview
@Composable
private fun EmptyScreen() {
    EmptyContent()
}